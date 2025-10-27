package com.zipcheck.android.data.model.map

import com.google.gson.annotations.SerializedName

data class AddrListRequest(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("radiusMeters") val radiusMeters: Int
)
