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

data class MyRiskItem(
    val riskId: Int,
    val userId: Int,
    val riskScore: Double,
    val riskLevel: String,
    val depositPct: Double,
    val average: Double,
    val minimum: Double,
    val maximum: Double,
    val maxPra: Double,
    val pra: Double,
    val standardDeviation: Double,
    val createdAt: String,
    val address: String,
    val addressDetail: String
)