package com.mahipal.phonewallpaper.network

import com.mahipal.phonewallpaper.mvvm.model.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface APIService {

    @Headers("Authorization: 563492ad6f91700001000001e904b14dfc22423982b60c0a40c8ef73")
    @GET("search/?")
    fun getImagesBySearch(
        @Query("page") pageNo:Int,
        @Query("per_page") perPageCount:Int,
        @Query("query") searchQuery: String?
    ): Call<BaseResponse>
}