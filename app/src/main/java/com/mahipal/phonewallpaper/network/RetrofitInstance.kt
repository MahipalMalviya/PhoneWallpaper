package com.mahipal.phonewallpaper.network

import android.content.Context
import com.mahipal.phonewallpaper.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor




object RetrofitInstance {

    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): APIService? {
        if (retrofit == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.IMAGE_BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
            if (BuildConfig.DEBUG) {
                retrofit.client(setLogging())
            }
            this.retrofit = retrofit.build()
        }
        return retrofit?.create(APIService::class.java)
    }

    fun setLogging(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}