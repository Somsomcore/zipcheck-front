package com.zipcheck.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zipcheck.android.data.repo.UserInfoRepository

class MyPageViewModelFactory(
    private val repo: UserInfoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
