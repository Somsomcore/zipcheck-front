package com.zipcheck.android.data.model.map

import com.google.gson.annotations.SerializedName

data class KakaoGeocodeResponse(
    val meta: Meta,
    val documents: List<Document>
)

data class Meta(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("pageable_count") val pageableCount: Int,
    @SerializedName("is_end") val isEnd: Boolean
)

data class Document(
    @SerializedName("address_name") val addressName: String,
    @SerializedName("y") val latitude: String, // 위도 (y)
    @SerializedName("x") val longitude: String, // 경도 (x)
    @SerializedName("address_type") val addressType: String,
    val address: Address? = null,
    @SerializedName("road_address") val roadAddress: RoadAddress? = null
)

data class Address(
    @SerializedName("address_name") val addressName: String
)

data class RoadAddress(
    @SerializedName("address_name") val addressName: String
)
