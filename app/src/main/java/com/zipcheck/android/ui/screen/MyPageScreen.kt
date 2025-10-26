package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.DarkBlack
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White

data class SettingsItem(val title: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(navController : NavHostController) {
    val menuItems = listOf(
        SettingsItem(title = "알림 설정", onClick = { println("알림 설정 클릭") }),
        SettingsItem(title = "내가 쓴 신고글", onClick = { println("신고글 클릭") }),
        SettingsItem(title = "로그아웃", onClick = { println("로그아웃 클릭") }),
    )

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("마이페이지", navController)
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
            ProfileHeader()

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
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 닉네임 및 이메일
            Column {
                Text(
                    text = "000 님",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 구글 아이콘 Placeholder (실제 아이콘 리소스 필요)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Black) // 구글 아이콘 자리
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "0000@gmail.com",
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