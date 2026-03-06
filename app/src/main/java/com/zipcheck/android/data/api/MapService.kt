package com.zipcheck.android.data.api

import com.zipcheck.android.data.model.map.AddrListResponse
import com.zipcheck.android.data.model.map.KakaoAddressResponse
import com.zipcheck.android.data.model.map.KakaoGeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapService {
    @GET("v2/local/search/keyword.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        @Query("page") page: Int = 1,     // 1~45
        @Query("size") size: Int = 15     // 1~30
    ): KakaoAddressResponse

    @GET("v2/local/search/address.json")
    suspend fun searchAddressResult(
        @Query("query") query: String,
        @Query("page") page: Int = 1,     // 1~45
        @Query("size") size: Int = 15     // 1~30
    ): KakaoAddressResponse

    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Query("query") query: String
    ): KakaoAddressResponse

    @GET("api/report/addrList")
    suspend fun getAddrList(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radiusMeters") radiusMeters: Int
    ): AddrListResponse
}

interface KakaoLocalService {
    @GET("v2/local/search/address.json")
    suspend fun geocode(@Query("query") query: String): KakaoAddressResponse
}

interface KakaoLocalKeywordService {
    // 기존 address.json을 keyword.json으로 변경하여 장소명 검색 지원
    @GET("v2/local/search/keyword.json")
    suspend fun geocode(@Query("query") query: String): KakaoAddressResponse
}

data class KakaoAddressResponse(val documents: List<KakaoAddressDoc>)
data class KakaoAddressDoc(val x: String, val y: String)