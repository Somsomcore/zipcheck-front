package com.zipcheck.android.data.repo

import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.model.report.MyRiskAnlyResponse
import com.zipcheck.android.data.model.report.RiskAnlyRequest
import com.zipcheck.android.data.model.report.RiskAnlyResponse
import retrofit2.Response

class RiskRepository(private val service: ReportService) {
    suspend fun analyze(accessToken: String, regionCode: String, req: RiskAnlyRequest): Response<RiskAnlyResponse> {
        val finalToken = "Bearer $accessToken"
        return service.analyzeRisk(finalToken, regionCode, req)
    }

    suspend fun getMyRiskList(
        accessToken: String,
        year: Int,
        month: Int,
        page: Int,
        size: Int
    ): Response<MyRiskAnlyResponse> {
        val bearer = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        return service.getMyRiskList(bearer, year, month, page, size)
    }
}