package com.zipcheck.android.ui.screen

import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zipcheck.android.R
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // NameInputScreen으로부터 결과를 받아서 처리하는 부분
    val signupResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("signup_result_key")?.observeAsState()

    LaunchedEffect(signupResult?.value) {
        if (signupResult?.value == "success") {
            // 결과가 "success"이면 스낵바를 띄웁니다.
            scope.launch {
                snackbarHostState.showSnackbar("회원가입이 완료되었습니다. 로그인을 진행해 주세요.")
            }
            // 스낵바를 한 번만 보여주기 위해 사용한 데이터를 제거합니다.
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("signup_result_key")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp), // 좌우 여백 약간 넓게
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 뒤로가기 아이콘
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // 화면 상단-로고 사이 비율 확보

            // 로고
            Image(
                painter = painterResource(id = R.drawable.login_icon),
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 로그인 / 회원가입 텍스트 + 구분선
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp
                )
                Text(
                    text = "로그인 / 회원가입",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 카카오 로그인 버튼
            Image(
                painter = painterResource(id = R.drawable.icon_kakao),
                contentDescription = "카카오 로그인",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // 버튼 높이 고정
                    .clickable { navController.navigate("login_screen_name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 네이버 로그인 버튼
            Image(
                painter = painterResource(id = R.drawable.icon_naver),
                contentDescription = "네이버 로그인",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // 버튼 높이 고정
                    .clickable { /* 네이버 로그인 로직 */ }
            )

            Spacer(modifier = Modifier.weight(2f)) // 화면 하단과 버튼 사이 여유 공간
        }
    }
}
