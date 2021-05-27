package com.bangkit.capstone.vision.ui.detail

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.ActivityDetailReportBinding
import com.bangkit.capstone.vision.model.ReportEntity
import com.bangkit.capstone.vision.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DetailReportActivity : AppCompatActivity() {

    private lateinit var activityDetailReportBinding: ActivityDetailReportBinding

    private lateinit var db: FirebaseFirestore

    private var report: ReportEntity? = null

    companion object {
        const val EXTRA_REPORT = "extra_report"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailReportBinding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(activityDetailReportBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        activityDetailReportBinding.progress.visibility = View.VISIBLE
        report = intent.getParcelableExtra(EXTRA_REPORT)
        if (report != null) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            storageRef.child("potholes/${report?.image}").getBytes(Long.MAX_VALUE)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    Glide.with(this)
                        .load(bitmap)
                        .apply(
                            RequestOptions.placeholderOf(R.drawable.ic_loading)
                                .error(R.drawable.ic_error)
                        )
                        .into(activityDetailReportBinding.imgRoad)
                    activityDetailReportBinding.progress.visibility = View.GONE
                }
            activityDetailReportBinding.tvBestAccuracy.text = report?.prediction
            activityDetailReportBinding.tvUploadedBy.text = report?.upload_by
            activityDetailReportBinding.tvTime.text = DateUtils.getFromDate(report!!.time)
            activityDetailReportBinding.tvAddress.text = "${report?.address} - ${report?.distance} from your location"
            activityDetailReportBinding.tvNote.text = report?.note
            when (report?.status) {
                "Pending" -> {
                    supportActionBar?.title = "Detail Report - Pending"
                }
                "Confirm" -> {
                    supportActionBar?.title = "Detail Report - Confirm"
                }
                "Finish" -> {
                    supportActionBar?.title = "Detail Report - Finish"
                }
                "Reject" -> {
                    supportActionBar?.title = "Detail Report - Reject"
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val maps = menu.findItem(R.id.maps)
        maps.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.maps -> {
                val intent = Intent(this, DetailMapsActivity::class.java)
                intent.putExtra(EXTRA_REPORT, report)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}