package com.zipcheck.android.data.model.report

import com.google.gson.annotations.SerializedName

data class Top5Response(
    @SerializedName("isSuccess") val isSuccess: Boolean? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result") val result: Top5Result? = null
)

data class Top5Result(
    @SerializedName("reports") val top5Reports: List<Top5Report>? = null
)

data class Top5Report(
    @SerializedName("reportId") val reportId: Int? = null,
    @SerializedName("addr") val address: String? = null,
    @SerializedName("addrDetail") val addressDetail: String? = null,
    @SerializedName("classification") val classifications: List<Classification>? = null,
    @SerializedName("contractType") val contractType: Int? = null,
    @SerializedName("count") val count: Int? = null
)

data class Classification(
    @SerializedName("classification") val classification: Int? = null
)

data class ReportItem(
    val typeId: Int,
    val typeName: String,
    val addr: String,
    val addrDetail: String,
    val countText: String,
    val chip1: String?,
    val chip2: String?
)

private val CONTRACTTYPE_MAP = mapOf(
    1 to "아파트",
    2 to "연립다세대",
    3 to "단독",
    4 to "다가구",
    5 to "오피스텔"
)

private val CLASSIFICATION_TAG_MAP = mapOf(
    1 to "#깡통전세",
    2 to "#전세사기",
    3 to "#불법전대",
    4 to "#명의도용",
    5 to "#기타"
)

fun Top5Report.toReportItem(): ReportItem {
    val typeId = contractType ?: -1
    val typeName = CONTRACTTYPE_MAP[typeId] ?: "기타"
    val tags = (classifications ?: emptyList())
        .mapNotNull { it.classification?.let(CLASSIFICATION_TAG_MAP::get) }
        .take(2)

    val (chip1, chip2) = when (tags.size) {
        0 -> "#분류없음" to "#분류없음"
        1 -> tags[0] to "#분류없음"
        else -> tags[0] to tags[1]
    }

    return ReportItem(
        typeId = typeId,
        typeName = typeName!!,
        addr = address.orEmpty(),
        addrDetail = addressDetail.orEmpty(),
        countText = "${count}회",
        chip1 = chip1,
        chip2 = chip2
    )
}