package com.zipcheck.android.data.repo

import com.zipcheck.android.data.api.MapService
import com.zipcheck.android.data.model.map.AddrListItem

class MapRepository(
    private val service: MapService
) {
    suspend fun fetchAddrList(
        lat: Double,
        lng: Double,
        radiusMeters: Int
    ): List<AddrListItem> {
        val res = service.getAddrList(lat, lng, radiusMeters)
        if (!res.isSuccess) return emptyList()
        return res.result.locations
    }
}