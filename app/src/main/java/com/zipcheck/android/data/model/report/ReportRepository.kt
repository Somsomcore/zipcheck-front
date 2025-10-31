package com.zipcheck.android.data.model.report

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.format.DateTimeFormatter

class ReportRepository(
    private val api: ReportApi,
    private val appContext: Context
) {
    private val text = "text/plain".toMediaType()
    private val pdf  = "application/pdf".toMediaType()
    private val dateFmt = DateTimeFormatter.ISO_DATE

    private fun String.asBody(): RequestBody = toRequestBody(text)

    private suspend fun uriToPdfPart(name: String, uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        return withContext(Dispatchers.IO) {
            val input = appContext.contentResolver.openInputStream(uri) ?: return@withContext null
            val cache = File(appContext.cacheDir, "evidence.pdf")
            cache.outputStream().use { out -> input.copyTo(out) }
            val body = cache.asRequestBody(pdf)
            MultipartBody.Part.createFormData(name, cache.name, body)
        }
    }

    suspend fun submitReport(
        addr: String,
        addrDetail: String,
        classification: Int,
        contractType: Int,
        content: String,
        contractAtEpochDay: Long,
        recognizedAtEpochDay: Long,
        evidencePdf: Uri?,
        page: Int = 0,
        size: Int = 10
    ): ReportPage {
        val contractAt = java.time.LocalDate.ofEpochDay(contractAtEpochDay).format(dateFmt)
        val recognizedAt = java.time.LocalDate.ofEpochDay(recognizedAtEpochDay).format(dateFmt)
        val pdfPart = uriToPdfPart("evidencePdf", evidencePdf)

        val resp = api.submitReport(
            addr = addr.asBody(),
            addrDetail = addrDetail.ifBlank { "" }.asBody(),
            classification = classification.toString().asBody(),
            contractType = contractType.toString().asBody(),
            content = content.asBody(),
            contractAt = contractAt.asBody(),
            recognizedAt = recognizedAt.asBody(),
            evidencePdf = pdfPart,
            page = page,
            size = size
        )
        require(resp.isSuccess && resp.result != null) { resp.message ?: "신고 접수 실패" }
        return resp.result!!.toModel()
    }

    suspend fun getReports(addr: String, page: Int = 0, size: Int = 10): ReportPage {
        val resp = api.getReports(addr, page, size)
        require(resp.isSuccess && resp.result != null) { resp.message ?: "신고 목록 조회 실패" }
        return resp.result!!.toModel()
    }
}