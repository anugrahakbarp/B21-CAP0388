package com.bangkit.capstone.vision.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getFromDate(date: String): String {
        val originalDate = SimpleDateFormat("yyyy/MM/dd H:mm:ss", Locale.US)
        val formatDate = SimpleDateFormat("d MMMM yyyy H:mm", Locale.US)
        val dateFormatted = originalDate.parse(date)
        return formatDate.format(dateFormatted)
    }

    fun getNowDate(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd H:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val netDate = Date()
        return sdf.format(netDate)
    }
}