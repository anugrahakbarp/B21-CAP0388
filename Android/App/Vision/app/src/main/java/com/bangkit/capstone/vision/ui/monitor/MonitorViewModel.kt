package com.bangkit.capstone.vision.ui.monitor

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.vision.databinding.FragmentMonitorBinding
import com.google.firebase.firestore.FirebaseFirestore

class MonitorViewModel : ViewModel() {

    private val allConfirm = MutableLiveData<Int>()
    private val allFinish = MutableLiveData<Int>()
    private val myConfirm = MutableLiveData<Int>()
    private val myFinish = MutableLiveData<Int>()
    private val myPending = MutableLiveData<Int>()
    private val myReject = MutableLiveData<Int>()
    private val allCount = MutableLiveData<List<Int>>()
    private val myCount = MutableLiveData<List<Int>>()

    fun getAllMonitorReports(
        binding: FragmentMonitorBinding
    ): MutableLiveData<List<Int>> {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports").whereEqualTo("status", "Confirm")
            .get()
            .addOnSuccessListener { confirmItem ->
                val c = ArrayList<Int>()
                if (confirmItem.isEmpty) {
                    Log.d("Confirm Status Report", "Empty")
                    c.add(0)
                    binding.progressBar.visibility = View.GONE
                } else {
                    c.add(confirmItem.documents.size)
                    allConfirm.postValue(confirmItem.documents.size)
                }
                db.collection("reports").whereEqualTo("status", "Finish")
                    .get()
                    .addOnSuccessListener { finishItem ->
                        if (finishItem.isEmpty) {
                            c.add(0)
                            Log.d("Finish Status Report", "Empty")
                            binding.progressBar.visibility = View.GONE
                            allCount.postValue(c)
                        } else {
                            c.add(finishItem.documents.size)
                            allFinish.postValue(finishItem.documents.size)
                            allCount.postValue(c)
                            Log.d("Finish Status Report", finishItem.toString())
                        }
                    }.addOnFailureListener {
                        Log.d("Failure", it.cause.toString())
                    }
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
            }
        return allCount
    }

    fun getMyMonitorReports(
        binding: FragmentMonitorBinding,
        username: String
    ): MutableLiveData<List<Int>> {
        val db = FirebaseFirestore.getInstance().collection("reports")
        db.whereEqualTo("status", "Confirm").whereEqualTo("upload_by", username)
            .get()
            .addOnSuccessListener { confirmItem ->
                val c = ArrayList<Int>()
                if (confirmItem.isEmpty) {
                    Log.d("Count Status Confirm", "Empty")
                    binding.progressBar.visibility = View.GONE
                    c.add(0)
                } else {
                    c.add(confirmItem.documents.size)
                    myConfirm.postValue(confirmItem.documents.size)
                }
                db.whereEqualTo("status", "Finish").whereEqualTo("upload_by", username)
                    .get()
                    .addOnSuccessListener { finishItem ->
                        if (finishItem.isEmpty) {
                            Log.d("Count Status Finish", "Empty")
                            c.add(0)
                            binding.progressBar.visibility = View.GONE
                        } else {
                            c.add(finishItem.documents.size)
                            myFinish.postValue(finishItem.documents.size)
                        }
                        db.whereEqualTo("status", "Pending").whereEqualTo("upload_by", username)
                            .get()
                            .addOnSuccessListener { pendingItem ->
                                if (pendingItem.isEmpty) {
                                    Log.d("Count Status Pending", "Empty")
                                    c.add(0)
                                    binding.progressBar.visibility = View.GONE
                                } else {
                                    c.add(pendingItem.documents.size)
                                    myPending.postValue(pendingItem.documents.size)
                                }
                                db.whereEqualTo("status", "Reject")
                                    .whereEqualTo("upload_by", username)
                                    .get()
                                    .addOnSuccessListener { rejectItem ->
                                        if (rejectItem.isEmpty) {
                                            c.add(0)
                                            Log.d("Count Status Reject", "Empty")
                                            binding.progressBar.visibility = View.GONE
                                            myCount.postValue(c)
                                        } else {
                                            c.add(rejectItem.documents.size)
                                            myReject.postValue(rejectItem.documents.size)
                                            myCount.postValue(c)
                                            binding.cvMyChart.visibility = View.VISIBLE
                                        }
                                    }.addOnFailureListener {
                                        Log.d("Failure", it.cause.toString())
                                    }
                            }.addOnFailureListener {
                                Log.d("Failure", it.cause.toString())
                            }
                    }.addOnFailureListener {
                        Log.d("Failure", it.cause.toString())
                    }
            }.addOnFailureListener {
                Log.d("Failure", it.cause.toString())
            }
        return myCount
    }
}