package com.zipcheck.android.data.model.map

import com.google.gson.annotations.SerializedName

data class AddrListResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("result") val result: AddrListResult
)
data class AddrListResult(
    @SerializedName("locations") val locations: List<AddrListItem>
)
data class AddrListItem(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String,
    @SerializedName("reportCount") val reportCount: Int
)
