package com.bangkit.capstone.vision.ui.addReport

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentAddReportBinding
import com.bangkit.capstone.vision.model.LocationModel
import com.bangkit.capstone.vision.tflite.Classifier
import com.bangkit.capstone.vision.tflite.TensorFlowImageClassifier
import com.bangkit.capstone.vision.ui.UserPreference
import com.bangkit.capstone.vision.utils.AppExecutors
import com.bangkit.capstone.vision.utils.DateUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.util.*


class AddReportFragment : Fragment() {

    companion object {
        private var EXTRA_LOCATION = "extra_location"
        private var EXTRA_DISTANCE = "extra_distance"
        private var VALIDATE_CODE = "validate"
        private var REPORT_CODE = "report"
        private var SNACKBAR_SUCCESS_CODE = "snackbar_success"
        private var SNACKBAR_ERROR_CODE = "snackbar_error"
        const val PICK_LOCATION_CODE = 202
        const val PERMISSION_CODE = 101
    }

    private lateinit var fragmentAddReportBinding: FragmentAddReportBinding

    private lateinit var db: FirebaseFirestore

    private var imageBitmap: Bitmap? = null

    private var location: String? = null

    private var distance: String? = null

    private lateinit var locationModel: LocationModel

    private val MODEL_PATH = "pothole-yolov4-int8.tflite"
    private val INPUT_SIZE = 416
    private val LABEL_PATH = "labels.txt"

    private lateinit var classifier: Classifier
    private val appExecutors: AppExecutors = AppExecutors()

    private fun processImage(image: Uri) {

        val progress = progress(VALIDATE_CODE)
        progress.show()
        progress.window!!.setLayout(500, 500)

        val bitmap =
            MediaStore.Images.Media.getBitmap(activity?.contentResolver, image)
        imageBitmap =
            Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
                .copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(imageBitmap!!)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.0f

        Thread(Runnable {

            val recognized: List<Classifier.Recognition> = classifier.recognizeImage(imageBitmap)

            if (recognized.isNotEmpty()) {
                val score = recognized.map {
                    it.confidence
                }

                if ((Collections.max(score)) > 0.5f) {
                    for (result in recognized) {
                        val location: RectF = result.location
                        canvas.drawRect(location, paint)
                    }
                    appExecutors.mainThread().execute {
                        fragmentAddReportBinding.tvTitleAccuracy.visibility = View.VISIBLE
                        fragmentAddReportBinding.tvAccuracy.visibility = View.VISIBLE
                        fragmentAddReportBinding.tvAccuracy.text = Collections.max(score).toString()
                        fragmentAddReportBinding.imgCaptured.setImageBitmap(imageBitmap)
                        progress.dismiss()
                        snackBar(
                            fragmentAddReportBinding.root,
                            getString(R.string.image_valid),
                            SNACKBAR_SUCCESS_CODE
                        ).show()
                    }
                } else {
                    appExecutors.mainThread().execute {
                        progress.dismiss()
                        snackBar(
                            fragmentAddReportBinding.root,
                            getString(R.string.image_invalid),
                            SNACKBAR_ERROR_CODE
                        ).show()
                        clearInvalidImage()
                    }
                }
            } else {
                appExecutors.mainThread().execute {
                    progress.dismiss()
                    snackBar(
                        fragmentAddReportBinding.root,
                        getString(R.string.cant_detect),
                        SNACKBAR_ERROR_CODE
                    ).show()
                    clearInvalidImage()
                }
            }
        }).start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initTensorFlowAndLoadModel()
        db = FirebaseFirestore.getInstance()

        fragmentAddReportBinding =
            FragmentAddReportBinding.inflate(layoutInflater, container, false)

        clearForm()

        fragmentAddReportBinding.btnPickImage.setOnClickListener {
            val cropperImage: Intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Images")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(INPUT_SIZE, INPUT_SIZE)
                .setCropMenuCropButtonIcon(R.drawable.ic_baseline_crop_24)
                .getIntent(requireContext())

            startActivityForResult(cropperImage, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        }

        fragmentAddReportBinding.edtLocation.setOnClickListener {
            startActivityForResult(
                Intent(activity, PickLocationActivity::class.java),
                PICK_LOCATION_CODE
            )
        }

        fragmentAddReportBinding.btnSend.setOnClickListener {
            validateForm()
        }

        return fragmentAddReportBinding.root
    }

    private fun validateForm() {
        val location = fragmentAddReportBinding.edtLocation.text.toString().trim()
        val note = fragmentAddReportBinding.edtNote.text.toString().trim()
        if (imageBitmap == null) {
            Toast.makeText(activity, getString(R.string.please_pick_image), Toast.LENGTH_LONG)
                .show()
        }
        if (distance == null) {
            Toast.makeText(activity, "Distance required !", Toast.LENGTH_LONG).show()
        }
        if (location.isEmpty()) {
            fragmentAddReportBinding.edtLocation.error = getString(R.string.required)
        }
        if (note.isEmpty()) {
            fragmentAddReportBinding.edtNote.error = getString(R.string.required)
        }
        if (imageBitmap != null && distance != null && location.isNotEmpty() && note.isNotEmpty()) {
            val confirmDialog = AlertDialog.Builder(activity)
            confirmDialog.setCancelable(false)
            confirmDialog.setTitle(getString(R.string.confirmation))
            confirmDialog.setMessage(getString(R.string.want_to_send_report))
            confirmDialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                uploadFile()
            }
            confirmDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            confirmDialog.show()
        }
    }

    private fun uploadFile() {
        val mUserPreference = UserPreference(requireContext())
        val mUserModel = mUserPreference.getUser()
        val imageKey = UUID.randomUUID().toString()

        val roadType = fragmentAddReportBinding.spnRoadType.selectedItem
        val location = fragmentAddReportBinding.edtLocation.text.toString().trim()
        val note = fragmentAddReportBinding.edtNote.text.toString().trim()
        val prediction = fragmentAddReportBinding.tvAccuracy.text.toString().trim()

        val reportId = db.collection("reports").document().id
        val reportRef = db.collection("reports").document(reportId)
        val report = HashMap<String, Any>()
        report["report_id"] = reportId
        report["road_type"] = roadType
        report["address"] = location
        report["distance"] = distance!!
        report["image"] = imageKey
        report["prediction"] = prediction
        report["location"] = GeoPoint(
            locationModel.locationLatitude.toDouble(),
            locationModel.locationLongitude.toDouble()
        )
        report["area"] = locationModel.locationAdmin
        report["sub_area"] = locationModel.locationSubAdmin
        report["locality"] = locationModel.locationLocality
        report["sub_locality"] = locationModel.locationSubLocality
        report["note"] = note
        report["time"] = DateUtils.getNowDate()
        report["upload_by"] = mUserModel.username.toString()
        report["status"] = "Pending"

        val progress = progress(REPORT_CODE)
        progress.show()
        progress.window!!.setLayout(500, 500)

        reportRef.set(report).addOnSuccessListener {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("potholes/$imageKey")

            imageBitmap?.let { image ->
                val imageByteArray = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray)
                val images = imageByteArray.toByteArray()
                imageRef.putBytes(images)
                    .addOnSuccessListener {
                        progress.dismiss()
                        snackBar(
                            fragmentAddReportBinding.root,
                            getString(R.string.report_uploaded),
                            SNACKBAR_SUCCESS_CODE
                        ).show()
                        clearForm()
                    }
                    .addOnFailureListener {
                        progress.dismiss()
                        snackBar(
                            fragmentAddReportBinding.root,
                            getString(R.string.report_failed_to_upload),
                            SNACKBAR_ERROR_CODE
                        ).show()
                        clearForm()
                    }
                    .addOnProgressListener {
                        val progressPercent =
                            (100.0 * it.bytesTransferred / it.totalByteCount)
                        progress.setTitle("Progress : $progressPercent %")
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "Upload Failure", Toast.LENGTH_SHORT).show()
            Log.d("Failure", it.cause.toString())
        }
    }

    private fun progress(status: String): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        if (status == VALIDATE_CODE) {
            builder.setView(R.layout.progress_validate)
        } else if (status == REPORT_CODE) {
            builder.setView(R.layout.progress_report)
        }
        builder.setCancelable(false)
        return builder.create()
    }

    private fun snackBar(view: View, message: String, status: String): Snackbar {
        val layout = fragmentAddReportBinding.clAddReportValid
        val snackBar = Snackbar.make(
            layout, message, Snackbar.LENGTH_LONG
        )
        val viewSnackBar = snackBar.view
        val params: FrameLayout.LayoutParams =
            view.layoutParams as FrameLayout.LayoutParams
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        view.layoutParams = params
        layout.bringToFront()
        if (status == SNACKBAR_SUCCESS_CODE) {
            viewSnackBar.setBackgroundColor(
                Color.TRANSPARENT
            )
            layout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.all_corner_ten_radius_green, null)
        } else if (status == SNACKBAR_ERROR_CODE) {
            viewSnackBar.setBackgroundColor(
                Color.TRANSPARENT
            )
            layout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.all_corner_ten_radius_red, null)
        }
        return snackBar
    }

    private fun clearForm() {
        view?.findFocus().let {
            val inputMethodManager =
                ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it?.windowToken, 0)
        }
        fragmentAddReportBinding.edtLocation.setText("")
        fragmentAddReportBinding.edtNote.setText("")
        fragmentAddReportBinding.tvAccuracy.text = ""
        fragmentAddReportBinding.tvAccuracy.visibility = View.GONE
        fragmentAddReportBinding.tvTitleAccuracy.visibility = View.GONE
        fragmentAddReportBinding.imgCaptured.setImageDrawable(null)
        imageBitmap = null
    }

    private fun clearInvalidImage() {
        fragmentAddReportBinding.imgCaptured.setImageDrawable(null)
        fragmentAddReportBinding.tvTitleAccuracy.visibility = View.GONE
        fragmentAddReportBinding.tvAccuracy.visibility = View.GONE
        fragmentAddReportBinding.tvAccuracy.text = ""
        imageBitmap = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    processImage(result.uri)
                } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                    fragmentAddReportBinding.tvAccuracy.text = ""
                    fragmentAddReportBinding.tvAccuracy.visibility = View.GONE
                    fragmentAddReportBinding.tvTitleAccuracy.visibility = View.GONE
                    fragmentAddReportBinding.imgCaptured.setImageDrawable(null)
                    imageBitmap = null
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    snackBar(
                        fragmentAddReportBinding.root,
                        getString(R.string.image_invalid),
                        SNACKBAR_ERROR_CODE
                    ).show()
                    Log.d("Cropping failed ", result.error.toString())
                }
            }
            PICK_LOCATION_CODE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val extras = data?.getSerializableExtra(EXTRA_LOCATION) as LocationModel
                    val extrasDistance = data.getSerializableExtra(EXTRA_DISTANCE) as String
                    fragmentAddReportBinding.edtLocation.setText(extras.locationAddress)
                    fragmentAddReportBinding.edtLocation.error = null
                    location = extras.locationAddress
                    distance = extrasDistance
                    locationModel = extras
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(activity, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initTensorFlowAndLoadModel() {
        Thread(Runnable {
            try {
                classifier = TensorFlowImageClassifier.create(
                    activity?.assets,
                    MODEL_PATH,
                    LABEL_PATH,
                    INPUT_SIZE
                )
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }).start()
    }
}