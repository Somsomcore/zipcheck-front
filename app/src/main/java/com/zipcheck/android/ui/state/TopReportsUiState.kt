package com.zipcheck.android.ui.state

import com.zipcheck.android.data.model.report.ReportItem

sealed interface TopReportsUiState {
    data object Loading : TopReportsUiState
    data class Success(val items: List<ReportItem>) : TopReportsUiState
    data class Error(val message: String) : TopReportsUiState
}