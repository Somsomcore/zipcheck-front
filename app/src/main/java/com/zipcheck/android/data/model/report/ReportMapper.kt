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
    memberId = memberId,
    reportId = reportId,
    registrationStatus = registrationStatus,
    createdAt = createdAt
)