package com.zipcheck.android.data.model.report

import com.google.gson.annotations.SerializedName

data class RiskAnlyResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: RiskResult
)

data class RiskResult(
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