package com.zipcheck.android.data.repo

import android.util.Log
import com.zipcheck.android.data.api.UserSevice
import com.zipcheck.android.data.model.user.UserResponse
import retrofit2.Response

class UserInfoRepository(private val userSevice: UserSevice) {
    suspend fun fetchUserInfo(accessToken: String): UserResponse {
        val finalToken = if (accessToken.startsWith("Bearer ")) {
            accessToken
        } else {
            "Bearer $accessToken"
        }

        val response = userSevice.getUserInfo(finalToken)
//
//        Log.e("UserAPI", "❌ HTTP ${response.code()} - ${response.message()}")
//        Log.e("UserAPI", "❌ errorBody: ${response.errorBody()?.string()}")
//// 요청 헤더 확인
//        val req = response.raw().request
//        Log.d("UserAPI", "➡️ ${req.method} ${req.url}")
//        Log.d("UserAPI", "➡️ Authorization: ${req.header("Authorization")}")
//        Log.d("UserAPI", "➡️ Accept: ${req.header("Accept")}")

        if (response.isSuccessful) {
            // 2. 응답 본문(Body)의 null 여부 확인
            val body = response.body()
            if (body != null) {
                // 최종 응답 모델 객체를 반환
                return body
            } else {
                // 본문이 null인 경우 (API 응답 구조 확인 필요)
                throw IllegalStateException("서버 응답 본문이 비어있습니다.")
            }
        } else {
            // HTTP 응답 실패 (4xx, 5xx 코드)
            Log.e("UserAPI", "❌ HTTP ${response.code()} - ${response.message()}")
            throw Exception("API 요청 실패: HTTP ${response.code()} - ${response.message()}")
        }
    }
}