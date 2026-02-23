package com.zipcheck.android.data.network

import android.util.Log
import com.zipcheck.android.data.api.AuthService
import com.zipcheck.android.data.api.RefreshTokenRequest
import com.zipcheck.android.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthAuthenticator(private val tokenManager: TokenManager) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val oldAccessToken = response.request.header("Authorization")
        if (oldAccessToken == null) {
            Log.d("AuthAuthenticator", "이전 요청에 Authorization 헤더가 없음. 갱신 불가.")
            return null // 헤더가 없으면 재시도하지 않음
        }

//        val oldAccessToken = tokenManager.getAccessToken()
        val refreshToken = runBlocking { tokenManager.getRefreshToken() }

        if (refreshToken.isNullOrBlank()) {
            Log.d("AuthAuthenticator", "RefreshToken 없음 → 로그인 필요")
            return null
        }

        // 새 Retrofit 인스턴스로 Refresh API 요청
        val refreshService = Retrofit.Builder()
            .baseUrl("http://3.39.188.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)

        return try {
            val refreshResponse = refreshService
                .refreshToken(
                    authorization = oldAccessToken, // 만료된 액세스 토큰 헤더 전달
                    body = RefreshTokenRequest(refreshToken) // 리프레시 토큰을 DTO로 감싸서 전달
                )
                .execute()

            if (refreshResponse.isSuccessful && refreshResponse.body()?.isSuccess == true) {
                val newAccessToken = refreshResponse.body()!!.result.accessToken
                val newRefreshToken = refreshResponse.body()!!.result.refreshToken

                runBlocking {
                    tokenManager.saveTokens(newAccessToken, newRefreshToken)
                }

                // 이전 요청에 새 토큰을 붙여서 재요청
                response.request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                Log.e("AuthAuthenticator", "Refresh 실패 → 로그인 필요")
                Log.e("AuthAuthenticator", "🔥 Refresh 실패 → ${refreshResponse.code()} / ${refreshResponse.body()?.message}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthAuthenticator", "토큰 갱신 중 오류", e)
            null
        }
    }
}