package com.zipcheck.android.ui

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.sdk.common.KakaoSdk
import com.zipcheck.android.R

class ZipCheckApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val nativeAppKey: String = getString(R.string.NATIVE_APP_KEY)
        // 카카오 SDK 초기화
        KakaoMapSdk.init(this, nativeAppKey)
        KakaoSdk.init(this, nativeAppKey)

    }
}