package com.zipcheck.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zipcheck.android.data.model.alarm.AlarmDTO
import com.zipcheck.android.data.network.RetrofitObj
import com.zipcheck.android.util.AlarmSseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmService = RetrofitObj.getAlarmService(application)
    private var sseManager: AlarmSseManager? = null

    private val _alarmEvent = MutableStateFlow<String?>(null)
    val alarmEvent = _alarmEvent.asStateFlow()

    private val _alarmList = MutableStateFlow<List<AlarmDTO>>(emptyList())
    val alarmList = _alarmList.asStateFlow()

    fun subscribeAlarm(token: String) {
        if (sseManager != null) return // 이미 존재하면 무시

        // 매니저 초기화 및 콜백 연결
        sseManager = AlarmSseManager(token) { data ->
            viewModelScope.launch {
                _alarmEvent.emit(data) // 데이터 수신 시 UI로 전달
            }
        }
        sseManager?.startSubscription()
    }

    override fun onCleared() {
        super.onCleared()
        sseManager?.stopSubscription() // 앱 종료 시 안전하게 연결 해제
        sseManager = null
    }

    // 알람 목록 조회
    fun fetchAlarms(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Bearer 붙여서 호출 (명세서 확인)
                val response = alarmService.getAlarmList("Bearer $token", page = 0, size = 20).execute()
                if (response.isSuccessful) {
                    _alarmList.value = response.body()?.result?.alarms ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 2. 알람 모두 읽음 처리 (확인)
    fun confirmAllAlarms(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                alarmService.confirmAlarm("Bearer $token").execute()
                // 확인 후 로컬 상태 업데이트 (모두 읽음으로 표시)
                _alarmList.value = _alarmList.value.map { it.copy(confirmed = true) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}