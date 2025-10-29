package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.user.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserSevice {
    @GET
    fun getUserInfo(@Header("Authorization") accessToken: String): Call<UserResponse>
}