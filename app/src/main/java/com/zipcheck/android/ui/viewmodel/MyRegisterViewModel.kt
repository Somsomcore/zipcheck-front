package com.zipcheck.android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zipcheck.android.data.model.mypage.RegistrationStatus
import com.zipcheck.android.data.model.mypage.toMyReportItem
import com.zipcheck.android.data.repo.ReportRepository
import com.zipcheck.android.ui.state.MyReportsUiState
import com.zipcheck.android.ui.state.UserInfoUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyRegisterViewModel(
    private val reportRepository: ReportRepository,
    // 실제 환경에서는 DI를 통해 토큰을 관리해야 합니다. 여기서는 예시를 위해 하드코딩합니다.
    private val dummyToken: String = "YOUR_ACCESS_TOKEN",
    private val status: RegistrationStatus,
    private val page: Int,
    private val size: Int
) : ViewModel() {

    // 외부에서 관찰(observe) 가능한 UI 상태
    private val _uiState = MutableStateFlow<MyReportsUiState>(MyReportsUiState.Loading)
    val uiState: StateFlow<MyReportsUiState> = _uiState.asStateFlow()

    init {
        fetchMyReports(
            token = dummyToken,
            status = status,
            page = page,
            size = size
        )
    }

    fun fetchMyReports(
        token: String,
        status: RegistrationStatus,
        page: Int,
        size: Int
    ) {
        viewModelScope.launch {
            _uiState.value = MyReportsUiState.Loading // 로딩 상태 설정

            val accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0IiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTc2MTg0Mjk0OCwiZXhwIjoxNzYxODQ2NTQ4fQ.uu_IJCZNDBmc9r1nGWQJoNwZPxZQZvU3unyl-C0CuDHMbCVnCbSKFKKsLzURY__wk_NzFrpnQnQ0RTihEgT6XQ"
            // val accessToken = token

            try {
                // Repository를 통해 API 호출 (suspend 함수 가정)
                val myRegisterResponse = reportRepository.getMyReport(accessToken, status, page, size)

                Log.d("", "✅ Response code: ${myRegisterResponse.code}")
                Log.d("UserAPI", "✅ isSuccessful: ${myRegisterResponse.isSuccess}")
                Log.d("UserAPI", "✅ message: ${myRegisterResponse.message}")

                if (myRegisterResponse.isSuccess) {
                    val result = myRegisterResponse.result

                    // **수정된 로직:** MyReports 리스트를 MyReportItem 리스트로 변환하여 Success 상태에 담습니다.
                    val reportItems = result.reports.map { it.toMyReportItem() }

                    Log.d("UserAPI", "✅ Report Items Count: ${reportItems.size}")
                    _uiState.value = MyReportsUiState.Success(
                        items = reportItems
                    )
                } else {
                    Log.e("UserAPI", "❌ HTTP ${myRegisterResponse.code} - ${myRegisterResponse.message}")
                    _uiState.value = MyReportsUiState.Error(myRegisterResponse.message)
                }
            } catch (e: Exception) {
                // 네트워크 오류, 파싱 오류 등의 예외 처리
                _uiState.value = MyReportsUiState.Error("데이터 로딩 실패: ${e.localizedMessage}")
            }
        }
    }
}