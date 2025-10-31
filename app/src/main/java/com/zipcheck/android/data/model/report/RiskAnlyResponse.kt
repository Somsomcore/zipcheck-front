package com.zipcheck.android.data.model.report

import com.google.gson.annotations.SerializedName
import com.zipcheck.android.data.model.riskAnalysis.RiskAnalysisResult

data class RiskAnlyResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: RiskResult
)

data class MyRiskAnlyResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: RiskList
)

data class RiskList(
    @SerializedName("dailyRiskList") val dailyRiskList: List<DailyRiskResult>,
    @SerializedName("listSize") val listSize: Int,
    @SerializedName("totalPage") val totalPage: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("isFirst") val isFirst: Boolean,
    @SerializedName("isLast") val isLast: Boolean
)

data class DailyRiskResult(
    @SerializedName("date") val date: String,
    @SerializedName("risks") val risks: List<RiskResult>
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

fun MyRiskItem.toRiskAnalysisResult(): RiskAnalysisResult =
    RiskAnalysisResult(
        riskId = riskId,
        userId = userId,
        riskScore = riskScore,
        riskLevel = riskLevel,
        depositPct = depositPct,
        average = average,
        minimum = minimum,
        maximum = maximum,
        maxPra = maxPra,
        pra = pra,
        standardDeviation = standardDeviation,
        createdAt = createdAt,
        address = address,
        addressDetail = addressDetail
    )