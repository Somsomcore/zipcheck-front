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
    @SerializedName("address") val address: String,
    @SerializedName("content") val content: String,
    @SerializedName("contractType") val contractType: String, // 계약형태 아파트
    @SerializedName("contractAt") val contractAt: String, // 계약일자
    @SerializedName("createdAt") val createdAt: String,
)

data class MyReportItem(
    val id: Int,
    val typeName: String,
    val addr: String,
    val addrDetail: String,
    val countText: String,
    val chip1: String?
)

private val CONTRACTTYPE_MAP = mapOf(
    1 to "아파트",
    2 to "연립다세대",
    3 to "단독",
    4 to "다가구",
    5 to "오피스텔"
)

private val CLASSIFICATION_TAG_MAP = mapOf(
    1 to "#깡통전세",
    2 to "#전세사기",
    3 to "#불법전대",
    4 to "#명의도용",
    5 to "#기타"
)

//fun MyReports.toMyReportItem(): MyReportItem {
//    val tags = (classifications ?: emptyList())
//        .mapNotNull { it.classification?.let(CLASSIFICATION_TAG_MAP::get) }
//        .take(2)
//
//    // ✅ tags 크기에 따라 안전하게 chip 설정
//    val chip1 = tags.getOrNull(0) ?: "#분류없음"
//
//    return MyReportItem(
//        id = id,
//        typeName = CONTRACTTYPE_MAP[id] ?: "기타",
//        addr = address,
//        addrDetail = addressDetail,
//        chip1 = chip1
//    )
//}