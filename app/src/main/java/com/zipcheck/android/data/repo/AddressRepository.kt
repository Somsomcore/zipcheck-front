package com.zipcheck.android.data.repo

import com.zipcheck.android.data.api.MapService
import com.zipcheck.android.ui.screen.AddressResult

class AddressRepository(private val service: MapService) {
    suspend fun search(query: String): List<AddressResult> {
        if (query.isBlank()) return emptyList()
        val res = service.searchAddress(query = query, page = 1, size = 15)
        return res.documents.map { doc ->
            AddressResult(
                zipCode = doc.road_address?.zone_no ?: "",
                roadAddress = doc.road_address?.address_name ?: (doc.address_name ?: ""),
                oldAddress = doc.address_name ?: ""
            )
        }
    }
}