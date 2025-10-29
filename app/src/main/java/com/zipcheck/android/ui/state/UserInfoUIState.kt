package com.zipcheck.android.ui.state

import com.zipcheck.android.data.model.user.UserResult

interface UserInfoUIState {
    data object Loading : UserInfoUIState
    data class Success(val user: UserResult) : UserInfoUIState
    data class Error(val message: String) : UserInfoUIState
}