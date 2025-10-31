package com.zipcheck.android.ui.viewmodel // 패키지명은 예시입니다.

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zipcheck.android.data.repo.UserInfoRepository
import com.zipcheck.android.ui.state.UserInfoUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {

    // 외부에서 관찰(observe) 가능한 UI 상태
    private val _uiState = MutableStateFlow<UserInfoUIState>(UserInfoUIState.Loading)
    val uiState: StateFlow<UserInfoUIState> = _uiState.asStateFlow()

    init {
        // ViewModel 생성 시 유저 정보 로딩 시작
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        // 비동기 처리를 위해 Coroutine 사용
        viewModelScope.launch {
            _uiState.value = UserInfoUIState.Loading // 로딩 상태 설정

            // NOTE: 실제 앱에서는 SharedPreferences나 다른 저장소에서 Access Token을 가져와야 합니다.
            val accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0IiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTc2MTg0MTEzNSwiZXhwIjoxNzYxODQ0NzM1fQ.lwc2Hup0GLBQ57MGxJKBJJKvBO6izS7uhIkhxfBIXivrN6MrkPlargNZxnMK_I_JEiEAKKSI6fPexYgQY3c6Aw"

            try {
                // Repository를 통해 API 호출 (suspend 함수 가정)
                val userResponse = userInfoRepository.fetchUserInfo(accessToken)

                Log.d("UserAPI", "✅ Response code: ${userResponse.code}")
                Log.d("UserAPI", "✅ isSuccessful: ${userResponse.isSuccess}")
                Log.d("UserAPI", "✅ message: ${userResponse.message}")

                if (userResponse.isSuccess) {
                    val body = userResponse.result
                    Log.d("UserAPI", "✅ body: $body")
                    _uiState.value = UserInfoUIState.Success(userResponse.result)
                } else {
                    Log.e("UserAPI", "❌ HTTP ${userResponse.code} - ${userResponse.message}")
                    _uiState.value = UserInfoUIState.Error(userResponse.message)
                }
            } catch (e: Exception) {
                // 네트워크 오류, 파싱 오류 등의 예외 처리
                _uiState.value = UserInfoUIState.Error("데이터 로딩 실패: ${e.localizedMessage}")
            }
        }
    }
}