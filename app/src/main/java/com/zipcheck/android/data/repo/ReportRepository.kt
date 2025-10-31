package com.zipcheck.android.data.repo

import android.util.Log
import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.model.mypage.MyReportResponse
import com.zipcheck.android.data.model.mypage.RegistrationStatus
import com.zipcheck.android.data.model.report.MyRiskAnlyResponse
import retrofit2.Response

class ReportRepository(
    private val reportService: ReportService
) {
    suspend fun getMyReport(
        token: String,
        status: RegistrationStatus,
        page: Int = 0,
        size: Int = 10
    ): MyReportResponse {
        val finalToken = if (token.startsWith("Bearer ")) {
            token
        } else {
            "Bearer $token"
        }

        val response = reportService.getMyReport(finalToken, status.apiStatus, page, size)
//
        Log.e("MyReportAPI", "❌ HTTP ${response.code()} - ${response.message()}")
        Log.e("MyReportAPI", "❌ errorBody: ${response.errorBody()?.string()}")
// 요청 헤더 확인
        val req = response.raw().request
        Log.d("MyReportAPI", "➡️ ${req.method} ${req.url}")
        Log.d("MyReportAPI", "➡️ Authorization: ${req.header("Authorization")}")
        Log.d("MyReportAPI", "➡️ Accept: ${req.header("Accept")}")

        if (response.isSuccessful) {
            // 2. 응답 본문(Body)의 null 여부 확인
            val body = response.body()
            if (body != null) {
                // 최종 응답 모델 객체를 반환
                return body
            } else {
                // 본문이 null인 경우 (API 응답 구조 확인 필요)
                throw IllegalStateException("서버 응답 본문이 비어있습니다.")
            }
        } else {
            // HTTP 응답 실패 (4xx, 5xx 코드)
            Log.e("MyReportAPI", "❌ HTTP ${response.code()} - ${response.message()}")
            throw Exception("API 요청 실패: HTTP ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getMyRiskList(
        accessToken: String,
        year: Int,
        month: Int,
        page: Int,
        size: Int
    ): Response<MyRiskAnlyResponse> {
        val bearer = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        return reportService.getMyRiskList(bearer, year, month, page, size)
    }

    // data/repo/MapRepository.kt
    data class ReportUi(
        val reportId: Long,
        val addr: String,
        val addrDetail: String?,
        val chipText: String,      // 예: "깡통전세"
        val contractTypeText: String, // 예: "전세금"
        val contractDateText: String, // 예: 2002.12.12
        val content: String?
    )

    private fun classificationToChipText(code: Int): String =
        when (code) {
            0 -> "깡통전세" // 백엔드 정의에 맞게 추가 매핑
            1 -> "전세사기 의심"
            else -> "기타"
        }

    private fun contractTypeToText(code: Int) = when (code) {
        0 -> "전세금"
        1 -> "보증금"
        else -> "계약 형태"
    }


    private fun isoToYmdDot(s: String?): String {
        if (s.isNullOrBlank()) return "-"
        // "2025-10-31T12:57:53.921Z" -> "2025.10.31"
        return try { s.substring(0,10).replace("-", ".") } catch (_: Exception) { "-" }
    }

    suspend fun fetchReportsByAddress(addr: String, page: Int = 0, size: Int = 10): List<ReportUi> {
        val res = reportService.getReports(addr = addr, page = page, size = size)
        return res.result.reports.map {
            ReportUi(
                reportId = it.reportId,
                addr = it.addr,
                addrDetail = it.addrDetail,
                chipText = classificationToChipText(it.classification),
                contractTypeText = contractTypeToText(it.contractType),
                contractDateText = isoToYmdDot(it.contractAt),
                content = it.content
            )
        }
    }

}
