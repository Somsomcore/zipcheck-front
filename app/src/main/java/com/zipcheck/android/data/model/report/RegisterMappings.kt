package com.zipcheck.android.data.model.report

object RegisterMappings {
    private val classificationMap = mapOf(
        "아파트/다세대" to 0,
        "빌라" to 1,
        "투룸+" to 2,
        "원룸" to 3,
        "오피스텔" to 4,
        "상가" to 5
    )
    private val contractTypeMap = mapOf(
        "전세" to 0,
        "월세" to 1,
        "매매" to 2,
        "단기임대" to 3,
    )

    fun classificationCode(label: String): Int? = classificationMap[label]
    fun contractTypeCode(label: String): Int? = contractTypeMap[label]
}