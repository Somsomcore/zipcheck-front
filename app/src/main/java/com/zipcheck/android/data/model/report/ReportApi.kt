package com.zipcheck.android.data.model.report

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ReportApi {
    @Multipart
    @POST("api/report")
    suspend fun submitReport(
        @Part("addr") addr: RequestBody,
        @Part("addrDetail") addrDetail: RequestBody?,
        @Part("classification") classification: RequestBody,
        @Part("contractType") contractType: RequestBody,
        @Part("content") content: RequestBody,
        @Part("contractAt") contractAt: RequestBody,
        @Part("recognizedAt") recognizedAt: RequestBody,
        @Part evidencePdf: MultipartBody.Part?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): BaseResponse<ReportPageDto>

    @GET("api/report")
    suspend fun getReports(
        @Query("addr") addr: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): BaseResponse<ReportPageDto>
}