package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.report.Top5Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ReportService {
    @GET("api/report/top5")
    fun getTop5Report(@Header("Authorization") accessToken: String): Call<Top5Response>
}