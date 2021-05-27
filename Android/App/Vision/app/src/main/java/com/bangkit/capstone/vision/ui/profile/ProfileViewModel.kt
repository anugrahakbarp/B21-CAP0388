package com.bangkit.capstone.vision.ui.profile

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileViewModel : ViewModel() {

    private val listUserReport = MutableLiveData<Int>()

    fun getUserReports(
        username: String,
        progressBar: ProgressBar
    ): LiveData<Int> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("upload_by", username)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("Get Pending Report", "Empty")
                    listUserReport.postValue(it.size())
                    progressBar.visibility = View.GONE
                } else {
                    listUserReport.postValue(it.size())
                }
                progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
                progressBar.visibility = View.GONE
            }
        return listUserReport
    }
}