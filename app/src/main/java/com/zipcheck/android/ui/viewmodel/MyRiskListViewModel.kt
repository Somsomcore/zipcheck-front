package com.zipcheck.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zipcheck.android.data.model.report.DailyRiskResult
import com.zipcheck.android.data.model.report.MyRiskItem
import com.zipcheck.android.data.model.report.RiskResult
import com.zipcheck.android.data.model.riskAnalysis.RiskAnalysisResult
import com.zipcheck.android.data.repo.RiskRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.flow.*

class MyRiskListViewModel(
    private val repo: RiskRepository,
    private val accessToken: String
) : ViewModel() {

    private val _items = MutableStateFlow<List<MyRiskItem>>(emptyList())
    val items: StateFlow<List<MyRiskItem>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(page: Int = 0, size: Int = 10) {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val res = repo.getMyRiskList(accessToken, year, month, page, size)
            _loading.value = false

            if (res.isSuccessful) {
                val body = res.body()
                val riskResults: List<RiskResult> = body?.result?.dailyRiskList
                    ?.flatMap { it.risks }
                    ?: emptyList()

                val myRiskItems: List<MyRiskItem> = riskResults.map { risk ->
                    MyRiskItem(
                        riskId = risk.riskId,
                        userId = risk.userId,
                        riskScore = risk.riskScore,
                        riskLevel = risk.riskLevel,
                        depositPct = risk.depositPct,
                        average = risk.average,
                        minimum = risk.minimum,
                        maximum = risk.maximum,
                        maxPra = risk.maxPra,
                        pra = risk.pra,
                        standardDeviation = risk.standardDeviation,
                        createdAt = risk.createdAt,
                        address = risk.address,
                        addressDetail = risk.addressDetail
                    )
                }

                _items.value = myRiskItems
            } else {
                _error.value = "HTTP ${res.code()}"
            }
        }
    }
}