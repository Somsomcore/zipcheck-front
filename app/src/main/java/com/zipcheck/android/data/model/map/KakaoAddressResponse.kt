package com.zipcheck.android.data.model.map

import com.google.gson.annotations.SerializedName

data class KakaoAddressResponse(
    val documents: List<KakaoAddressDoc>,
    val meta: KakaoMeta
)

data class KakaoAddressDoc(
    @SerializedName("address_name") val addressName: String?,      // 전체 주소 (문자열)
    @SerializedName("address_type") val addressType: String?,      // REGION_ADDR / ROAD_ADDR 등
    val address: KakaoJibunAddress?,                               // 지번 주소
    @SerializedName("road_address_name") val roadAddress: String?, // 도로명 주소
    val x: String?,
    val y: String?
)

data class KakaoJibunAddress(
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("b_code") val bCode: String?,                  // ✅ 법정동 코드
    @SerializedName("h_code") val hCode: String?,                  // ✅ 행정동 코드
    @SerializedName("main_address_no") val mainAddressNo: String?,
    @SerializedName("sub_address_no") val subAddressNo: String?,
    @SerializedName("zip_code") val zipCode: String?
)

data class KakaoRoadAddress(
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("zone_no") val zoneNo: String?,
)

data class KakaoMeta(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("is_end") val isEnd: Boolean
)