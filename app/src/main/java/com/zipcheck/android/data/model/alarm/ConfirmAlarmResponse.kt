package com.zipcheck.android.data.model.alarm

data class ConfirmAlarmResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ConfirmAlarmResult
)

data class ConfirmAlarmResult(
    val confirmedCount: Int,
    val confirmedAt: String
)
