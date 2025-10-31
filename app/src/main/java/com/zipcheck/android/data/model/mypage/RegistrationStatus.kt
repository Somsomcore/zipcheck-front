package com.zipcheck.android.data.model.mypage

enum class RegistrationStatus(val apiStatus: String) {
    PENDING("received"), // 접수됨 (RegistrationStatus.PENDING)
    APPROVED("registered"); // 등록됨 (RegistrationStatus.APPROVED)
}