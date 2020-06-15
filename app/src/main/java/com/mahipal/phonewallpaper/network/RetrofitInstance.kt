package com.mahipal.phonewallpaper.network

import android.content.Context
import com.mahipal.phonewallpaper.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): APIService? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.IMAGE_BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit?.create(APIService::class.java)
    }
}