package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.map.KakaoAddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("v2/local/search/address.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        @Query("page") page: Int = 1,     // 1~45
        @Query("size") size: Int = 15     // 1~30
    ): KakaoAddressResponse
}