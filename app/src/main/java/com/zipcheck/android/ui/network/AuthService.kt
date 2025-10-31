package com.zipcheck.android.ui.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class SocialLoginRequest(
    val provider: String,
    val accessToken: String
)

data class SocialLoginResponse(
    val isSuccess: Boolean,
    val code: String?,
    val message: String?,
    val result: LoginResult?
)

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String?,         // nullable 유지
    val email: String,
    val role: String
)

//본인 인증
data class VerificationCodeRequest(val phone: String)
data class VerificationCodeResponse(
    val isSuccess: Boolean,
    val message: String?,
    val result: String?
)

data class VerifyCodeRequest(val verificationCode: String)
data class VerifyCodeResponse(
    val isSuccess: Boolean,
    val message: String?,
    val result: String?
)

// 테스트 토큰
data class TestTokenRequest(val userId: Int, val email: String)
data class TestTokenResponse(
    val isSuccess: Boolean,
    val result: LoginResult?
)

interface AuthService {
    @POST("/api/auth")
    fun socialLogin(
        @Header("Authorization") authorization: String?,
        @Body body: SocialLoginRequest
    ): Call<SocialLoginResponse>

    @POST("/api/auth/verification-code")
    fun sendVerificationCode(
        @Header("Authorization") authorization: String,
        @Body body: VerificationCodeRequest
    ): Call<VerificationCodeResponse>

    @POST("/api/auth/verification")
    fun verifyCode(
        @Header("Authorization") authorization: String,
        @Body body: VerifyCodeRequest
    ): Call<VerifyCodeResponse>

    @POST("/api/auth/test-token")
    fun testToken(
        @Header("Authorization") authorization: String,
        @Body body: TestTokenRequest
    ): Call<TestTokenResponse>
}