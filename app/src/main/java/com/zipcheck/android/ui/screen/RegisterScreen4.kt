package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.Gray // 회색 대신 PlaceholderGray나 LightGray를 사용하면 좋지만, 기존 테마에 맞춰 Gray 사용
import com.zipcheck.android.ui.theme.Grey90
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme

// Note: CustomTopBar는 이전 코드에서 가져왔다고 가정합니다.
// 이 화면은 LinearProgressIndicator가 필요 없으며, CustomTopBar는 '사기 등록' 제목만 표시합니다.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen4(navController: NavHostController) {

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("사기 등록", navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp), // 좌우 패딩을 이미지에 맞게 조정
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. 메인 내용 섹션 (상단)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 남은 공간을 차지하여 콘텐츠를 중앙에 배치
                    .padding(top = 100.dp), // 상단 여백을 충분히 줍니다.
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // Column 내부에서 상단 정렬
            ) {
                // 성공 체크 아이콘
                Box(
                    modifier = Modifier
                        .size(80.dp) // 이미지와 유사한 크기
                        .clip(CircleShape)
                        // .background(MainBlue) // <--- XML 파일이 이미 배경색(파란색 원)을 가지고 있으므로 제거
                        .padding(0.dp), // 내부 패딩을 0으로 설정하거나 제거하여 아이콘이 Box 전체를 채우도록 함
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        // 이 리소스 ID (ic_fraud_select)가 파란색 원 + 흰색 체크 XML이라고 가정
                        painter = painterResource(id = R.drawable.ic_click_after),
                        contentDescription = "Success",
                        // XML에 정의된 색상 (파란색 원 + 흰색 체크)을 그대로 사용하도록 Unspecified 설정
                        tint = Color.Unspecified,
                        // Box 크기에 맞게 아이콘 크기를 조정
                        modifier = Modifier.size(80.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "정상적으로 등록이 완료되었습니다",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "작성하신 신고글은 관리자 승인 후 등록됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black
                )
            }

            // 2. 추가 정보 및 버튼 섹션 (하단)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 주의사항 박스
                // 이미지의 밝은 회색 배경을 위한 임시 색상 정의 (테마에 맞게 조정 필요)
                val LightGrayBackground = Color(0xFFF5F5F5)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = LightGrayBackground, // 이미지의 밝은 회색 배경
                            shape = RoundedCornerShape(8.dp) // 모서리를 둥글게
                        )
                        .padding(16.dp) // 박스 전체 패딩
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp), // 아이콘과 텍스트 사이 간격
                        verticalAlignment = Alignment.Top,
                    ) {
                        // 문서 아이콘 (ic_document 리소스 사용 가정)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_document),
                            contentDescription = "Info",
                            tint = Color.DarkGray, // 이미지와 비슷한 어두운 회색
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp) // 텍스트와의 시각적 정렬을 위해 약간의 패딩 조정
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            // 첫 번째 문장
                            Text(
                                text = "작성하신 신고 글은 익명으로 저장되며, 개인정보는 노출되지 않습니다.\n제출하신 신고 글은 심사를 거친 뒤 등록됩니다.\n" +
                                        "심사 과정 중, 신고 글 수정을 요청 받으실 수 있습니다.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), // 이미지와 유사한 폰트 크기로 조정
                                color = Color.DarkGray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp)) // 박스와 버튼 사이 간격
                // 확인 버튼
                Button(
                    onClick = {
                        // 1) register_graph 목적지 id를 가져온다
                        val registerGraphId = try {
                            navController.getBackStackEntry("register_graph").destination.id
                        } catch (e: Exception) {
                            null
                        }

                        if (registerGraphId != null) {
                            navController.navigate("main_screen") {
                                // 문자열 route 대신 id로 popUpTo (가장 호환성이 좋음)
                                popUpTo(registerGraphId) {
                                    inclusive = true
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        } else {
                            // 혹시 그래프가 스택에 없으면 그냥 바로 이동
                            navController.navigate("main_screen") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("확인", color = White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterCompleteScreen() {
    ZipcheckfrontTheme {
        RegisterScreen4(navController = rememberNavController())
    }
}