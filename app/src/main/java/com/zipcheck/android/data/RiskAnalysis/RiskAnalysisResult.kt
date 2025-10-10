package com.zipcheck.android.data.RiskAnalysis

import java.time.LocalDate

data class RiskAnalysisResult(
    val id: Int,
    val date: LocalDate, // 날짜별 그룹화를 위해 LocalDate 추가
    val address: String, // 경기도 구리시 인창2로 65 (인창동)
    val apartment: String, // 힐스테이트 구리역 105동 1604호
    val riskPercentage: Int, // 88, 60 등
    val riskLevel: String, // 아주 위험, 의심
    val note: String // 유사 매물 대비 보증금이 10% 높습니다.
)
// 날짜별로 그룹화된 데이터 구조
data class AnalysisGroup(
    val date: LocalDate,
    val results: List<RiskAnalysisResult>
)

// 🎁 더미 데이터
val dummyResults = listOf(
    RiskAnalysisResult(1, LocalDate.of(2025, 9, 14), "경기도 구리시 인창2로 65 (인창동)", "힐스테이트 구리역 105동 1604호", 88, "아주 위험", "유사 매물 대비 보증금이 10% 높습니다"),
    RiskAnalysisResult(2, LocalDate.of(2025, 9, 14), "경기도 구리시 인창2로 65 (인창동)", "힐스테이트 구리역 105동 1604호", 60, "의심", "유사 매물 대비 보증금이 10% 높습니다"),
    RiskAnalysisResult(3, LocalDate.of(2025, 9, 16), "경기도 구리시 인창2로 65 (인창동)", "힐스테이트 구리역 105동 1604호", 88, "아주 위험", "유사 매물 대비 보증금이 10% 높습니다"),
    RiskAnalysisResult(4, LocalDate.of(2025, 9, 16), "경기도 구리시 인창2로 65 (인창동)", "힐스테이트 구리역 105동 1604호", 60, "의심", "유사 매물 대비 보증금이 10% 높습니다"),
    RiskAnalysisResult(5, LocalDate.of(2025, 9, 17), "경기도 구리시 인창2로 65 (인창동)", "힐스테이트 구리역 105동 1604호", 88, "아주 위험", "유사 매물 대비 보증금이 10% 높습니다"),
)
// 날짜별로 그룹화
val groupedResults = dummyResults
    .groupBy { it.date }
    .map { (date, results) -> AnalysisGroup(date, results) }
    .sortedByDescending { it.date } // 최신 날짜부터 표시