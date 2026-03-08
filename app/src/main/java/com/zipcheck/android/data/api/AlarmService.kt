package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.alarm.AlarmResponseDTO
import com.zipcheck.android.data.model.alarm.ConfirmAlarmResponse
import com.zipcheck.android.data.model.alarm.UnreadStatusResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AlarmService{
    @POST("api/alarm/confirm")
    fun confirmAlarm(
        @Header("Authorization") accessToken: String
    ): Call<ConfirmAlarmResponse>

    @GET("api/alarm")
    fun getAlarmList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<AlarmResponseDTO>

    @GET("api/alarm/unread-status")
    fun unreadStatus(
        @Header("Authorization") accessToken: String
    ): Call<UnreadStatusResponse>
}