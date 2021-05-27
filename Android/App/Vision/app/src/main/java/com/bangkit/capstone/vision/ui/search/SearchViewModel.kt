package com.bangkit.capstone.vision.ui.search

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.vision.databinding.FragmentSearchBinding
import com.bangkit.capstone.vision.model.ReportEntity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel : ViewModel() {

    private val listReport = MutableLiveData<List<ReportEntity>>()

    private lateinit var data: ReportEntity

    fun setSearchReports(
        address: String,
        context: Context,
        binding: FragmentSearchBinding
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereNotIn("status", arrayListOf("Pending", "Reject"))
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Search Report", "Empty")
                    Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.imgNoData.visibility = View.VISIBLE
                } else {
                    val report = ArrayList<ReportEntity>()
                    it.map { item ->
                        if (item.getString("address")!!.lowercase(Locale.getDefault()).contains(
                                address.lowercase(Locale.getDefault()), false
                            )
                        ) {
                            data = ReportEntity(
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
                            report.add(data)
                        }
                    }
                    listReport.postValue(report)
                    binding.progressBar.visibility = View.GONE
                }
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                binding.progressBar.visibility = View.GONE
            }
    }

    fun getSearchReports(): LiveData<List<ReportEntity>> {
        return listReport
    }
}