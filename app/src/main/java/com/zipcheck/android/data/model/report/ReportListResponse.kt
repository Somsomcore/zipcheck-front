package com.zipcheck.android.data.model.report

data class ReportListResponse(
    val isSuccess: Boolean,
    val code: String?,
    val message: String?,
    val result: ReportListResult
)

data class ReportListResult(
    val reports: List<ReportDto>,
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Long,
    val isLast: Boolean
)

data class ReportDto(
    val reportId: Long,
    val addr: String,
    val addrDetail: String?,
    val classification: Int,  // 백엔드 enum -> int 라면 그대로
    val contractType: Int,    // 백엔드 enum -> int 라면 그대로
    val content: String?,
    val contractAt: String?,  // "2025-10-31T12:57:53.921Z"
    val createdAt: String?
)

