package com.zipcheck.android.data.model.alarm

data class AlarmResponseDTO(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: AlarmResultDTO
)

data class AlarmResultDTO(
    val alarms: List<AlarmDTO>,
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Int,
    val isLast: Boolean
)

data class AlarmDTO(
    val alarmId: Int,
    val notificationType: String,
    val notificationContent: String,
    val reportId: Int,
    val confirmed: Boolean,
    val createdAt: String
)
