package com.bangkit.capstone.vision.ui.allReport

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.vision.databinding.FragmentConfirmedBinding
import com.bangkit.capstone.vision.databinding.FragmentFinishedBinding
import com.bangkit.capstone.vision.model.ReportEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AllReportViewModel : ViewModel() {

    private val listConfirmedReport = MutableLiveData<List<ReportEntity>>()
    private val listFinishedReport = MutableLiveData<List<ReportEntity>>()

    fun getAllConfirmedReports(
        binding: FragmentConfirmedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Confirm")
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

    fun getAllConfirmedReportsDesc(
        binding: FragmentConfirmedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Confirm")
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

    fun getAllFinishedReports(
        binding: FragmentFinishedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Finish")
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

    fun getAllFinishedReportsDesc(
        binding: FragmentFinishedBinding
    ): LiveData<List<ReportEntity>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Finish")
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

}