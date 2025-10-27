package com.zipcheck.android.data.model.report

import com.google.gson.annotations.SerializedName

data class Top5Response(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Top5Result
)

data class Top5Result(
    @SerializedName("reports") val top5Reports: List<Top5Report>
)

data class Top5Report(
    @SerializedName("reportId") val reportId: Int,
    @SerializedName("addr") val address: String,
    @SerializedName("addrDetail") val addressDetail: String,
    @SerializedName("classification") val classification: Int,
    @SerializedName("contractType") val contractType: Int,
    @SerializedName("count") val count: Int
)