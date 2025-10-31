package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.mypage.MyReportResponse
import com.zipcheck.android.data.model.report.MyRiskAnlyResponse
import com.zipcheck.android.data.model.report.ReportListResponse
import com.zipcheck.android.data.model.report.RiskAnlyRequest
import com.zipcheck.android.data.model.report.RiskAnlyResponse
import com.zipcheck.android.data.model.report.Top5Response
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportService {
    @GET("api/report/top5")
    fun getTop5Report(@Header("Authorization") accessToken: String): Call<Top5Response>

    @GET("api/report/my/{status}")
    suspend fun getMyReport(@Header("Authorization") accessToken: String, @Path("status") status: String, @Query("page") page: Int, @Query("size") size: Int): Response<MyReportResponse>

    @POST("api/real-estate/rent/analyze/{regionCode}")
    suspend fun analyzeRisk(@Header("Authorization") accessToken: String, @Path("regionCode") regionCode: String, @Body riskAnalysisRequest: RiskAnlyRequest): Response<RiskAnlyResponse>

    @GET("api/risks/my-list")
    suspend fun getMyRiskList(
        @Header("Authorization") auth: String,        // "Bearer <token>"
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<MyRiskAnlyResponse>

    @GET("api/report")
    suspend fun getReports(
        @Query("addr") addr: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ReportListResponse
}