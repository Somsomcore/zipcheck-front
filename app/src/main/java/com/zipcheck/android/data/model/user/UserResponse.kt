package com.zipcheck.android.data.model.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: UserResult
)

data class UserResult(
    @SerializedName("name") val name: Int,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("oauthType") val oauthType: String,
    @SerializedName("email") val email: String
)

