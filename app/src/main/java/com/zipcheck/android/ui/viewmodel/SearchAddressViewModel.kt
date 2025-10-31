package com.zipcheck.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zipcheck.android.data.repo.AddressRepository
import com.zipcheck.android.ui.screen.AddressResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AddressUiState {
    data object Idle: AddressUiState
    data object Loading: AddressUiState
    data class Success(val items: List<AddressResult>): AddressUiState
    data class Error(val message: String): AddressUiState
}

class SearchAddressViewModel(
    private val repo: AddressRepository
): ViewModel() {
    private val _ui = MutableStateFlow<AddressUiState>(AddressUiState.Idle)
    val ui: StateFlow<AddressUiState> = _ui

    private var typingJob: Job? = null

    fun onQueryChange(q: String) {
        typingJob?.cancel()
        if (q.isBlank()) {
            _ui.value = AddressUiState.Idle
            return
        }
        typingJob = viewModelScope.launch {
            _ui.value = AddressUiState.Loading
            // 디바운스 300ms
            delay(300)
            runCatching { repo.search(q) }
                .onSuccess { _ui.value = AddressUiState.Success(it) }
                .onFailure { _ui.value = AddressUiState.Error(it.message ?: "검색 실패") }
        }
    }
}