package com.zipcheck.android.ui.state

import com.zipcheck.android.data.model.report.RiskResult

sealed interface RiskUiState {
    data object Idle : RiskUiState
    data object Loading : RiskUiState
    data class Success(val data: RiskResult) : RiskUiState
    data class Error(val message: String) : RiskUiState
}