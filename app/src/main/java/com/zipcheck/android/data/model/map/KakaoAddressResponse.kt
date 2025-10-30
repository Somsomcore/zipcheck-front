package com.zipcheck.android.data.model.map

data class KakaoAddressResponse(
    val documents: List<KakaoAddressDoc>,
    val meta: KakaoMeta
)

data class KakaoAddressDoc(
    val address_name: String?,           // 전체 지번 주소
    val road_address: KakaoRoadAddress?, // 도로명 주소 객체
    val x: String?,                      // 경도
    val y: String?                       // 위도
)

data class KakaoRoadAddress(
    val address_name: String?,           // 전체 도로명 주소
    val zone_no: String?                 // 우편번호(도로명 기준)
)

data class KakaoMeta(
    val total_count: Int,
    val is_end: Boolean
)
