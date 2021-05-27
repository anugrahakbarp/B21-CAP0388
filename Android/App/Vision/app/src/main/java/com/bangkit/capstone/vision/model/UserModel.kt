package com.bangkit.capstone.vision.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var username: String? = null
) : Parcelable
