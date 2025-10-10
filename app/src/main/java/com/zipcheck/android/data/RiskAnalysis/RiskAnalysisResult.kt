package com.zipcheck.android.data.RiskAnalysis

data class RiskAnalysisResult(
    val id: Int,
    val address: String, // 경기도 구리시 인창2로 65 (인창동)
    val apartment: String, // 힐스테이트 구리역 105동 1604호
    val riskPercentage: Int, // 88, 60 등
    val riskLevel: String, // 아주 위험, 의심
    val note: String // 유사 매물 대비 보증금이 10% 높습니다.
)
