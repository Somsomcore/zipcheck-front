package com.zipcheck.android.data.model.report

object RegisterMappings {
    private val classificationMap = mapOf(
        "깡통전세" to 0,
        "전세 보증금 부풀림(시세 조작)" to 1,
        "불량 임대사업자 명의 이전" to 2,
        "건물 전체 전세 사기" to 3, // 공백 제거됨
        "근저당 선순위 설정 사기" to 4,
        "전월세 이중계약" to 5,
        "동일 물건 이중~삼중 계약" to 6,
        "신탁사 소유 물건 사기" to 7,
        "전세 대출 사기" to 8,
        "일반적인/기타 사기" to 9,
        "해당 유형 없음" to 10
    )

    // 2. 주택 형태 매핑 (RegisterScreen2의 contractTypeOptions에 해당)
    private val contractTypeMap = mapOf(
        "아파트" to 0,
        "연립다세대" to 1,
        "단독" to 2,
        "다가구" to 3,
        "오피스텔" to 4
    )

    fun classificationCode(label: String): Int? = classificationMap[label]
    fun contractTypeCode(label: String): Int? = contractTypeMap[label]
}