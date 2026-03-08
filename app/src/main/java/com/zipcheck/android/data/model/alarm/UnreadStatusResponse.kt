package com.zipcheck.android.data.model.alarm

data class UnreadStatusResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)
