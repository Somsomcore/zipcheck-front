package com.zipcheck.android.data.repo

import com.zipcheck.android.data.api.MapService
import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.model.report.RiskAnlyRequest
import com.zipcheck.android.data.model.report.RiskAnlyResponse
import retrofit2.Response

class RiskRepository(private val service: ReportService) {
    suspend fun analyze(accessToken: String, regionCode: String, req: RiskAnlyRequest): Response<RiskAnlyResponse> {
        val finalToken = "Bearer $accessToken"
        return service.analyzeRisk(finalToken, regionCode, req)
    }
}