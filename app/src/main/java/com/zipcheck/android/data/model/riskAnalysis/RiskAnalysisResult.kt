package com.zipcheck.android.data.model.riskAnalysis

import com.zipcheck.android.data.model.report.MyRiskItem
import java.time.LocalDate

data class RiskAnalysisResult(
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
// 날짜별로 그룹화된 데이터 구조
data class AnalysisGroup(
    val date: LocalDate,
    val results: List<MyRiskItem>
)
