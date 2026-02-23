package com.zipcheck.android.data.network

import com.zipcheck.android.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // No-Auth 요청은 헤더 제거 후 그대로 진행
        if (request.header("No-Auth") == "true") {
            return chain.proceed(
                request.newBuilder().removeHeader("No-Auth").build()
            )
        }

        val accessToken = runBlocking { tokenManager.getAccessToken() }
        val authedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authedRequest)
    }
}