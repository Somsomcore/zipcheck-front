package com.zipcheck.android.data.model.report

import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// 데이터 전송을 위한 간단한 DTO
data class ReportRequest(
    val addr: String,
    val addrDetail: String,
    val classification: Int,
    val contractType: Int,
    val contractedAt: String,
    val recognitionAt: String,
    val content: String,
)

interface ReportApi {
    @Multipart
    @POST("api/report")
    suspend fun submitReport(
        @Part("request") request: ReportRequest,
        @Part file: MultipartBody.Part?
    ): BaseResponse<ReportPageDto>

    @GET("api/report")
    suspend fun getReports(
        @Query("addr") addr: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): BaseResponse<ReportPageDto>
}