package com.zipcheck.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zipcheck.android.data.repo.UserInfoRepository

class MyPageViewModelFactory(
    private val repo: UserInfoRepository,
    private val accessToken: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(repo, accessToken) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
