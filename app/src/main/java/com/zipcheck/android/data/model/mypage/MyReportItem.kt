package com.zipcheck.android.data.model.mypage

import com.google.gson.annotations.SerializedName

data class MyReportResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: MyReportResult
)

data class MyReportResult(
    @SerializedName("reports") val reports: List<MyReports>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("isLast") val isLast: Boolean
)

data class MyReports(
    @SerializedName("id") val id: Int,
    @SerializedName("addr") val address: String,
    @SerializedName("addrDetail") val addressDetail: String,
    @SerializedName("content") val content: String,
    @SerializedName("contractType") val contractType: String, // 계약형태 아파트
    @SerializedName("classification")  val classificationId: Int,
    @SerializedName("classificationName") val classificationName: String,
    @SerializedName("contractedAt") val contractAt: String, // 계약일자
    @SerializedName("createdAt") val createdAt: String,
)

data class MyReportItem(
    val id: Int,
    val addr: String,
    val addrDetail: String,
    val content: String,
    val contractType: String,
    val classificationId: Int,
    val classificationName: String,
    val contractAt: String
)

fun MyReports.toMyReportItem(): MyReportItem {
    return MyReportItem(
        id = id,
        addr = address,
        addrDetail = addressDetail,
        content = content,
        contractType = contractType,
        classificationId = classificationId,
        classificationName = classificationName,
        contractAt = contractAt
    )
}