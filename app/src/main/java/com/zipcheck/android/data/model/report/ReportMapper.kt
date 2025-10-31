package com.zipcheck.android.data.model.report

fun ReportItemDto.toModel() = Report(
    reportId = reportId,
    addr = addr,
    addrDetail = addrDetail,
    classification = classification,
    contractType = contractType,
    content = content,
    contractAt = contractAt,
    createdAt = createdAt
)

fun ReportPageDto.toModel() = ReportPage(
    reports = reports.map { it.toModel() },
    totalPages = totalPages,
    currentPage = currentPage,
    totalElements = totalElements,
    isLast = isLast
)