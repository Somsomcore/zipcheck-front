package com.zipcheck.android.data.network

import android.content.Context
import com.kakao.sdk.network.ApiFactory.loggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObj {
    // base url
    private const val BASE_URL = "http://default-zipcheck-service-e1cc1-111600641-5f88f4b7229b.kr.lb.naverncp.com/"

    fun getRetrofit(context: Context): Retrofit {
//        val tokenManager = TokenManager(context)

//        val loggingInterceptor = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

            // ✅ 1. LoggingInterceptor 먼저 추가
            .addInterceptor(loggingInterceptor)

            // ✅ 2. AccessToken 헤더 추가
//            .addInterceptor(AuthInterceptor(tokenManager))
//
//            // ✅ 3. 401 Unauthorized 처리
//            .authenticator(AuthAuthenticator(tokenManager))

            // ✅ 4. refresh token 만료 시 로그인 화면으로
//            .addInterceptor(ResponseInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}