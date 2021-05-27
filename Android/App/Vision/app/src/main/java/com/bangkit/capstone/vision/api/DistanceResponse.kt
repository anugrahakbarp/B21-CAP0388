package com.bangkit.capstone.vision.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DistanceResponse(
	val destinationAddresses: List<String>,
	val rows: List<RowsItem>,
	val status: String,
	val originAddresses: List<String>
) : Parcelable

@Parcelize
data class RowsItem(
	val elements: List<ElementsItem>
) : Parcelable

@Parcelize
data class ElementsItem(
	val duration: Duration,
	val distance: Distance,
	val status: String
) : Parcelable

@Parcelize
data class Distance(
	val text: String,
	val value: Int
) : Parcelable

@Parcelize
data class Duration(
	val text: String,
	val value: Int
) : Parcelable
