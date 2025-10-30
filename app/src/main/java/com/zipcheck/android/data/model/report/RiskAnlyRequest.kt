package com.zipcheck.android.data.model.report

data class RiskAnlyRequest(
    val deposit: Int, // 보증금
    val propertyType: String, // 매물 종류
    val area: Int, // 면적
    val floor: Int, // 층
    val buildYear: Int,
    val address: String,
    val addressDetail: String
)
