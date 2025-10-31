package com.zipcheck.android.data.model.report

data class BaseResponse<T>(
    val isSuccess: Boolean,
    val code: String?,
    val message: String?,
    val result: T?
)

data class ReportItemDto(
    val reportId: Long,
    val addr: String,
    val addrDetail: String?,
    val classification: Int,
    val contractType: Int,
    val content: String,
    val contractAt: String,
    val createdAt: String
)

data class ReportPageDto(
    val reports: List<ReportItemDto>,
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Int,
    val isLast: Boolean
)
