package com.zipcheck.android.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zipcheck.android.R
import com.zipcheck.android.data.api.AuthService
import com.zipcheck.android.data.api.LogoutResult
import com.zipcheck.android.data.api.UserSevice
import com.zipcheck.android.data.network.RetrofitObj
import com.zipcheck.android.data.repo.UserInfoRepository
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.state.UserInfoUIState
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.DarkBlack
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.viewmodel.MyPageViewModel
import com.zipcheck.android.ui.viewmodel.MyPageViewModelFactory
import retrofit2.Call

data class SettingsItem(val title: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    navController : NavHostController,
    accessToken: String
) {
    val context = navController.context
    val scope = rememberCoroutineScope() // 비동기 작업을 위한 스코프

    // 로그아웃 함수 정의
    val onLogoutClick = {
        val shared = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val accessToken = shared.getString("accessToken", "") ?: ""

        val authService = RetrofitObj.getRetrofit(context).create(AuthService::class.java)

        // 서버에 로그아웃 요청
        authService.logout("Bearer $accessToken").enqueue(object : retrofit2.Callback<LogoutResult> {
            override fun onResponse(call: Call<LogoutResult>, response: retrofit2.Response<LogoutResult>) {
                // 서버 응답 성공 여부와 상관없이 로컬 토큰은 삭제하고 이동 (사용자 경험 우선)
                shared.edit().clear().apply()

                navController.navigate("login_screen") {
                    // 메인 스택을 비워서 뒤로가기로 마이페이지에 못 오게 함
                    popUpTo(0) { inclusive = true }
                }
                android.widget.Toast.makeText(context, "로그아웃 되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<LogoutResult>, t: Throwable) {
                // 네트워크 오류 시에도 일단 로그아웃 처리
                shared.edit().clear().apply()
                navController.navigate("login_screen") { popUpTo(0) }
            }
        })
    }

    val menuItems = listOf(
        SettingsItem(title = "내가 쓴 신고글", onClick = { navController.navigate("my_register_screen") }),
        SettingsItem(title = "로그아웃", onClick = onLogoutClick),
    )

    val repo = remember {
        UserInfoRepository(
            RetrofitObj.getRetrofit(navController.context).create(UserSevice::class.java)
        )
    }
    val factory = remember { MyPageViewModelFactory(repo, accessToken) }

    val viewModel: MyPageViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("마이페이지", navController, "main_screen")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TopBar)
            )

            // 2. 프로필 정보 영역
            when (uiState) {
                is UserInfoUIState.Loading -> {
                    // 로딩 중일 때 로딩 UI 표시 (예: ProgressBar 또는 스켈레톤 UI)
                    ProfileHeader(name = "로딩 중...", email = "로딩 중...", oath = "로딩 중")
                    // TODO: 실제 로딩 인디케이터로 교체
                }
                is UserInfoUIState.Error -> {
                    // 에러 발생 시 에러 메시지 표시
                    val errorMessage = (uiState as UserInfoUIState.Error).message
                    ProfileHeader(name = "오류", email = errorMessage, oath = "오류")
                    // TODO: 에러 처리 및 재시도 버튼 추가
                }
                is UserInfoUIState.Success -> {
                    // 성공 시 실제 유저 데이터 표시
                    val user = (uiState as UserInfoUIState.Success).user
                    ProfileHeader(
                        name = user.name, // 이름 표시
                        email = user.email,         // 이메일 표시
                        oath = user.oauthType // 프로필 URL 전달
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(TopBar)
            )

            // 3. 설정 항목 목록 (반복)
            menuItems.forEach { item ->
                SettingsMenuItem(item.title, item.onClick)
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    email: String,
    oath: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 닉네임 및 이메일
            Column {
                Text(
                    text = name + " 님",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 구글 아이콘 Placeholder (실제 아이콘 리소스 필요)
                    if (oath == "KAKAO") {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_kakao),
                                contentDescription = "kakao"
                            )
                        }
                    } else if (oath == "NAVER"){
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_naver),
                                contentDescription = "kakao"
                            )
                        }
                    } else if (oath == "Google") {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.login_icon),
                                contentDescription = "kakao"
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.Black) // 구글 아이콘 자리
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = DarkBlack
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsMenuItem(
    title: String, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp), // 상하 여백으로 항목 높이 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Black,
            fontWeight = FontWeight.Medium
        )
    }
}