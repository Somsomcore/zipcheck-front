package com.zipcheck.android.data.network

import android.content.Context
import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

// KakaoRetrofit.kt
object KakaoRetrofit {
    private const val BASE_URL = "https://dapi.kakao.com/"

    fun getRetrofit(context: Context, restApiKey: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val auth = Interceptor { chain ->
            val pkg = context.packageName
            val pm = context.packageManager
            val verName = try {
                pm.getPackageInfo(pkg, 0).versionName ?: "1.0"
            } catch (_: Exception) { "1.0" }

            // REST API 키 정제
            val token = restApiKey.trim()
                .replace("\uFEFF","")
                .replace("\n","")
                .replace("\r","")

            // ✅ KA 헤더 구성 (os / origin 필수)
            val ka = buildString {
                append("sdk/1.0 ")
                append("os/android-${Build.VERSION.SDK_INT} ")
                append("lang/${Locale.getDefault()} ")
                append("device/${Build.MODEL} ")
                append("origin/$pkg ")
                append("app/$pkg ")
                append("ver/$verName")
            }

            val req = chain.request().newBuilder()
                .header("Authorization", "KakaoAK $token") // REST API 키
                .header("KA", ka)                          // ✅ 필수 헤더
                // 선택: User-Agent 있으면 도움 될 때 있음
                .header(
                    "User-Agent",
                    "Android ${Build.VERSION.RELEASE}; ${Build.MODEL}"
                )
                .build()

            chain.proceed(req)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
