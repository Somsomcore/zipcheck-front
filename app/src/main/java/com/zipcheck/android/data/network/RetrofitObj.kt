package com.zipcheck.android.data.network

import android.content.Context
import com.kakao.sdk.network.ApiFactory.loggingInterceptor
import com.zipcheck.android.data.api.AlarmService
import com.zipcheck.android.util.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObj {
    // base url
    private const val BASE_URL = "http://172.30.1.78:8080/"

    fun getRetrofit(context: Context): Retrofit {
        val tokenManager = TokenManager(context)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(AuthAuthenticator(tokenManager))
            .addInterceptor(ResponseInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAlarmService(context: Context): AlarmService {
        return getRetrofit(context).create(AlarmService::class.java)
    }
}