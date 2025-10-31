package com.zipcheck.android.ui.state

import com.zipcheck.android.data.model.mypage.MyReportItem

sealed interface MyReportsUiState {
    data object Loading : MyReportsUiState
    data class Success(val items: List<MyReportItem>) : MyReportsUiState
    data class Error(val message: String) : MyReportsUiState
}