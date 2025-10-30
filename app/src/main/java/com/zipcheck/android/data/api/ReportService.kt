package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.mypage.MyReportResponse
import com.zipcheck.android.data.model.report.Top5Response
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportService {
    @GET("api/report/top5")
    fun getTop5Report(@Header("Authorization") accessToken: String): Call<Top5Response>

    @GET("api/report/my/{status}")
    suspend fun getMyReport(@Header("Authorization") accessToken: String, @Path("status") status: String, @Query("page") page: Int, @Query("size") size: Int): Response<MyReportResponse>
}