package com.zipcheck.android.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.TextFieldBorderGray
import com.zipcheck.android.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSecondScreen(navController: NavController) {
    var floor by remember { mutableStateOf("") }

    var houseYear by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    // ModalBottomSheet를 위한 상태 변수들
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    // ModalBottomSheet를 먼저 선언합니다.
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            // 바텀 시트에 표시될 컨텐츠
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 선택 가능한 목록
                val houseYears = listOf("2022", "2023", "2024", "2025", "1990", "1991")

                // ✅ 표시용 하이라이트: 선택된 값이 있으면 그 값, 없으면 첫 번째 항목
                val highlighted = houseYear ?: houseYears.first()

                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp), // 너무 길어지지 않도록 최대 높이 설정 (선택 사항)
                    horizontalAlignment = Alignment.CenterHorizontally // LazyColumn 내부 아이템도 중앙 정렬
                ) {
                    items(houseYears.size) { index ->
                        val type = houseYears[index]

                        // 선택/비선택 색상
                        val isSelected = highlighted == type
                        val textColor = if (isSelected) MainBlue else TextFieldBorderGray // 회색 톤은 원하는 값으로

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    houseYear = type // 선택된 값을 houseType에 저장
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false // 애니메이션 완료 후 바텀 시트 숨김
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp, // 폰트 크기 증가
                                color = textColor // 선택 시 파란색으로 변경
                            )
                        }
                    }
                }
            }
        }
    }

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
            progress = 2f / 3f, // 첫 번째 화면이므로 33% 진행률
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp),
            color = MainBlue, // 파란색
            trackColor = Gray // 배경색
        )

        // 필수 정보 섹션
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "선택 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "입력 시 평가 위험도의 정확도가 높아져요.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        SearchTextField(
            label = "층수",
            value = floor,
            onValueChange = { floor = it },
            trailingIcon = { Text(text = "층", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.padding(16.dp))

        ClickableTextField(
            label = "건축 년도",
            value = houseYear, // 선택된 값을 표시
            placeholderText = "",
            leadingIcon = null,
            onClick = {
                focusManager.clearFocus()
                showBottomSheet = true // 클릭 시 바텀 시트 표시
            },
            trailingIcon = { Text(text = "년", color = Color.Gray) }
        )

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
                containerColor = MainBlue,
                disabledContainerColor = Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                color = White,
                fontSize = 18.sp
            )
        }
    }
}