package com.mahipal.phonewallpaper.mvvm.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahipal.phonewallpaper.mvvm.model.BaseResponse
import com.mahipal.phonewallpaper.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotoViewModel : ViewModel() {

    var mutablePhotoList = MutableLiveData<BaseResponse>()

    fun searchImages(context: Context, pageNo:Int,searchQuery: String?) {
        val apiService = RetrofitInstance.getInstance(context)
        val apiCall = apiService?.getImagesBySearch(pageNo,15,searchQuery)

        apiCall?.enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    mutablePhotoList.postValue(response.body())
                }
            }
        })
    }
}