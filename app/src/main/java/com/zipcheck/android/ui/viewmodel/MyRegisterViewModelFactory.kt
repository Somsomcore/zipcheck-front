package com.zipcheck.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zipcheck.android.data.model.mypage.RegistrationStatus
import com.zipcheck.android.data.repo.ReportRepository

class MyRegisterViewModelFactory(
    private val repo: ReportRepository,
    private val dummyToken: String,
    val status: RegistrationStatus,
    val page: Int,
    val size: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyRegisterViewModel::class.java)) {
            return MyRegisterViewModel(repo, dummyToken, status, page, size) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}