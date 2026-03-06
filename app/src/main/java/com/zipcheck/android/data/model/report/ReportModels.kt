package com.zipcheck.android.data.model.report

import android.net.Uri
import java.time.LocalDate

data class Report(
    val reportId: Long,
    val addr: String,
    val addrDetail: String?,
    val classification: Int,
    val contractType: Int,
    val content: String,
    val contractAt: String,
    val createdAt: String
)

data class ReportPage(
    val memberId: Int,
    val reportId: Int,
    val registrationStatus: String,
    val createdAt: String
)

data class ReportForm(
    val addr: String = "",
    val addrDetail: String = "",
    val classification: Int? = null,
    val contractType: Int? = null,
    val contractAt: LocalDate? = null,
    val recognizedAt: LocalDate? = null,
    val content: String = "",
    val evidencePdf: Uri? = null
)