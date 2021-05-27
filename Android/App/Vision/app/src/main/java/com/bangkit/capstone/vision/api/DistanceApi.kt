package com.bangkit.capstone.vision.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceApi {
    @GET("maps/api/distancematrix/json")
    fun getDistance(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("key") key: String
    ): Call<DistanceResponse>
}