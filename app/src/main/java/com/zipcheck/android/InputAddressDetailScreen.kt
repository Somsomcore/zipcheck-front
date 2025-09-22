package com.zipcheck.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.White

@Composable
fun InputAddressDetailScreen(navController: NavController, roadAddress: String) {
    // 텍스트 필드에 입력된 값을 저장하는 상태 변수
    // 매물 주소는 읽기 전용으로 설정
    val propertyAddress = roadAddress
    var detailAddress by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    // 모든 필드가 채워졌는지 확인하는 변수
    val allFieldsFilled = propertyAddress.isNotEmpty() && detailAddress.isNotEmpty()

    // 화면 전체를 Column으로 구성
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                // `detectTapGestures`를 사용해 탭(터치)이 발생했을 때 키보드를 내립니다.
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
    ) {
        // 상단 바 (뒤로 가기 버튼과 제목)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // 뒤로 가기 버튼
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .padding(start = 4.dp)
                    .clickable { navController.popBackStack() } // 클릭 시 이전 화면으로 돌아감
            )
            // 화면 제목
            Text(
                text = "위험도 조회",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // ✅ LinearProgressIndicator 추가
        LinearProgressIndicator(
            progress = 1f / 3f, // 첫 번째 화면이므로 33% 진행률
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp),
            color = MainBlue, // 파란색
            trackColor = Gray // 배경색
        )

        // 필수 정보 섹션
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "주소를 입력해주세요",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "상세주소까지 입력해주세요.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 입력 필드 섹션
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ✅ 매물 주소와 상세 주소 그룹화
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = propertyAddress,
                    onValueChange = {}, // Read-only이므로 비워둡니다.
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_location), // 지도 핀 아이콘
                            contentDescription = "Location icon",
                            tint = Gray
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent, // Added for disabled state
                        focusedBorderColor = Gray,
                        unfocusedBorderColor = Gray,
                        disabledBorderColor = Gray,
                    )
                )

                OutlinedTextField(
                    value = detailAddress,
                    onValueChange = { detailAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("상세주소 입력", color = PlaceholderGray) // Changed placeholder to match image
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = MainBlue,
                        unfocusedBorderColor = Gray,
                        disabledBorderColor = Gray,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
        }

        // ✅ 하단 버튼을 위로 밀어내는 Spacer
        Spacer(modifier = Modifier.weight(1f))

        // 다음 버튼
        Button(
            onClick = { /* 다음 화면으로 이동 로직 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (allFieldsFilled) MainBlue else Gray,
                disabledContainerColor = Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                color = if (allFieldsFilled) White else Black,
                fontSize = 18.sp
            )
        }
    }
}