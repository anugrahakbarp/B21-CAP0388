package com.bangkit.capstone.vision.utils

import com.github.mikephil.charting.formatter.ValueFormatter

class AxisFormatter(private val mValues: Array<String>) :  ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value >= 0) {
            if (mValues.size > value.toInt()) {
                mValues[value.toInt()]
            } else ""
        } else {
            ""
        }
    }
}