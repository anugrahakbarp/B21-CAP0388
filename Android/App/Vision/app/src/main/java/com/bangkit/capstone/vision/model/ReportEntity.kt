package com.bangkit.capstone.vision.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportEntity(
    var report_id: String,
    var address: String,
    var distance: String,
    var note: String? = null,
    var image: String,
    var prediction: String,
    var latitude: Double,
    var longitude: Double,
    var area: String,
    var sub_area: String,
    var locality: String,
    var sub_locality: String,
    var status: String,
    var upload_by: String,
    var time: String
) : Parcelable