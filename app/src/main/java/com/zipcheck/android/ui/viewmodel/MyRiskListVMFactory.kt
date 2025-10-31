package com.zipcheck.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zipcheck.android.data.repo.RiskRepository

class MyRiskListVMFactory(
    private val repo: RiskRepository,
    private val accessToken: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyRiskListViewModel(repo, accessToken) as T
    }
}
