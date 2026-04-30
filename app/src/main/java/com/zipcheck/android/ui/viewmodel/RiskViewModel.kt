package com.zipcheck.android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zipcheck.android.data.model.report.RiskAnlyRequest
import com.zipcheck.android.data.repo.RiskRepository
import com.zipcheck.android.ui.state.RiskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RiskViewModel(
    private val repo: RiskRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<RiskUiState>(RiskUiState.Idle)
    val ui: StateFlow<RiskUiState> = _ui

    fun analyze(accessToken: String, regionCode: String, req: RiskAnlyRequest) {
        viewModelScope.launch {
            Log.d("RiskViewModel", "🚀 analyze() 호출됨")
            Log.d("RiskViewModel", "🧩 regionCode = $regionCode")
            Log.d("RiskViewModel", "📦 Request = $req")
            _ui.value = RiskUiState.Loading
            try {
                val res = repo.analyze(accessToken, regionCode, req)

                Log.d("RiskViewModel", "✅ HTTP 코드: ${res.code()}")
                Log.d("RiskViewModel", "✅ HTTP 메시지: ${res.body()}")

                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    _ui.value = RiskUiState.Success(res.body()!!.result)
                } else {
                    Log.e("RiskViewModel", "❌ HTTP 오류: ${res.code()} / ${res.message()}")

                    _ui.value = RiskUiState.Error(
                        res.body()?.message ?: "분석 실패: ${res.code()}"
                    )
                }
            } catch (e: Exception) {
                _ui.value = RiskUiState.Error("네트워크 오류: ${e.localizedMessage}")
            }
        }
    }
}