package com.zipcheck.android.data.network

import android.content.Context
import android.content.Intent
import android.util.Log
import com.zipcheck.android.ui.screen.LoginScreen
import com.zipcheck.android.ui.screen.MainActivity
import okhttp3.Interceptor
import okhttp3.Response

class ResponseInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.e("ResponseInterceptor", "401 Unauthorized 감지 → 로그인 이동")

            // 토큰 삭제
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            // 로그인 이동
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // 로그인 화면으로 바로 가야 한다는 플래그
                putExtra("REASON", "AUTH_FAILED")
            }
            context.startActivity(intent)
        }

        return response
    }
}