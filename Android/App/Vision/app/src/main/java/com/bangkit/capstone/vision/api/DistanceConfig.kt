package com.bangkit.capstone.vision.api

import com.bangkit.capstone.vision.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DistanceConfig private constructor() {
    val retrofit: Retrofit

    companion object {
        private var self: DistanceConfig? = null
        val instance: DistanceConfig?
            get() {
                if (self == null) {
                    synchronized(DistanceConfig::class.java) {
                        if (self == null) {
                            self = DistanceConfig()
                        }
                    }
                }
                return self
            }
    }

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient: OkHttpClient =
            OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        val builder = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
        retrofit = builder.client(httpClient).build()
    }
}