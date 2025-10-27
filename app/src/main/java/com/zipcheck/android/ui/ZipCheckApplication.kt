package com.zipcheck.android.ui

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.KakaoMapSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.zipcheck.android.R

class ZipCheckApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val nativeAppKey = getString(R.string.NATIVE_APP_KEY)
        KakaoSdk.init(this, nativeAppKey)
        KakaoMapSdk.init(this, nativeAppKey)
        val keyHash = Utility.getKeyHash(this)
        Log.d("KAKAO_KEY_HASH", "keyHash = $keyHash")
        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.Naver_Client_ID),
            getString(R.string.Naver_Client_SECRET),
            getString(R.string.Naver_App_Name)
        )
    }
}