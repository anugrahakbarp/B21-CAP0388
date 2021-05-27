package com.bangkit.capstone.vision.ui.myReport

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.vision.databinding.FragmentConfirmedBinding
import com.bangkit.capstone.vision.databinding.FragmentFinishedBinding
import com.bangkit.capstone.vision.databinding.FragmentPendingBinding
import com.bangkit.capstone.vision.databinding.FragmentRejectedBinding
import com.bangkit.capstone.vision.model.ReportEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyReportViewModel : ViewModel() {

    private val listPendingReport = MutableLiveData<List<ReportEntity>>()
    private val listConfirmedReport = MutableLiveData<List<ReportEntity>>()
    private val listFinishedReport = MutableLiveData<List<ReportEntity>>()
    private val listRejectedReport = MutableLiveData<List<ReportEntity>>()

    fun getMyPendingReports(
        username: String,
        binding: FragmentPendingBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Pending")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Pending Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listPendingReport.postValue(data)
                }
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listPendingReport
    }

    fun getMyPendingReportsDesc(
        username: String,
        binding: FragmentPendingBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Pending")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Pending Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listPendingReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listPendingReport
    }

    fun getMyConfirmedReports(
        username: String,
        binding: FragmentConfirmedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Confirm")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Confirmed Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listConfirmedReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listConfirmedReport
    }

    fun getMyConfirmedReportsDesc(
        username: String,
        binding: FragmentConfirmedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Confirm")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Confirmed Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listConfirmedReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listConfirmedReport
    }

    fun getMyFinishedReports(
        username: String,
        binding: FragmentFinishedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Finish")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Finished Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listFinishedReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listFinishedReport
    }

    fun getMyFinishedReportsDesc(
        username: String,
        binding: FragmentFinishedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Finish")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Finished Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listFinishedReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listFinishedReport
    }

    fun getMyRejectedReports(
        username: String,
        binding: FragmentRejectedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Reject")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Rejected Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listRejectedReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listRejectedReport
    }

    fun getMyRejectedReportsDesc(
        username: String,
        binding: FragmentRejectedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Reject")
            .whereEqualTo("upload_by", username)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Rejected Report", "Empty")
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val data = it.map { item ->
                        ReportEntity(
                            item.getString("report_id").toString(),
                            item.getString("address").toString(),
                            item.getString("distance").toString(),
                            item.getString("note").toString(),
                            item.getString("image").toString(),
                            item.getString("prediction").toString(),
                            item.getGeoPoint("location")!!.latitude,
                            item.getGeoPoint("location")!!.longitude,
                            item.getString("area").toString(),
                            item.getString("sub_area").toString(),
                            item.getString("locality").toString(),
                            item.getString("sub_locality").toString(),
                            item.getString("status").toString(),
                            item.getString("upload_by").toString(),
                            item.getString("time").toString()
                        )
                    }.toList()
                    listRejectedReport.postValue(data)
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
        return listRejectedReport
    }
}