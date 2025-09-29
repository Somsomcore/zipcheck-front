package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.TextFieldBorderGray
import kotlinx.coroutines.launch

/**
 * 통신사 선택 화면. (CarrierInputScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierInputScreen(
    navController: NavController,
    name: String? // NameInputScreen에서 전달받은 이름
) {
    val carriers = listOf("SKT", "KT", "LG U+", "SKT 알뜰폰", "KT 알뜰폰", "LG U+ 알뜰폰")
    val LightGray = Color(0xFFF0F0F0) // "취소" 버튼 배경을 위한 밝은 회색

    var selectedCarrier by remember { mutableStateOf("") }
    var showCarrierSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Box를 사용하여 메인 콘텐츠와 하단 버튼을 분리하여 배치
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. 메인 콘텐츠 영역 (이름, 통신사 입력 필드)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 뒤로가기 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(24.dp)
                    .padding(top = 16.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 제목
            Text(
                text = "통신사를 선택해 주세요",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 1. 통신사 선택 필드
            Text(text = "통신사", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = selectedCarrier,
                onValueChange = { /* 텍스트 입력 방지 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .clickable { showCarrierSheet = true },
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        // R.drawable.ic_dropdown 대신, 예시 이미지에 맞는 드롭다운 아이콘 사용
                        painter = painterResource(id = R.drawable.ic_dropdown),
                        contentDescription = "Select Carrier",
                        modifier = Modifier.clickable { showCarrierSheet = true }
                    )
                },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    focusedBorderColor = MainBlue,
                    unfocusedBorderColor = TextFieldBorderGray
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 2. 이름 표시 필드
            Text(text = "이름", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = name ?: "",
                onValueChange = { /* 수정 불가 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                readOnly = true,
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    focusedBorderColor = MainBlue,
                    unfocusedBorderColor = TextFieldBorderGray
                )
            )

            // 하단 버튼이 Box의 최하단에 고정되므로, 콘텐츠가 버튼과 겹치지 않도록 충분한 공간 확보
            Spacer(modifier = Modifier.height(64.dp))
        }

        // ✅ 2. 하단 버튼 영역 (사진과 동일하게 화면 최하단에 고정)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter) // Box의 최하단에 고정
        ) {
            // 취소 버튼 (사진처럼 배경색 흰색, 텍스트 검정)
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(0.dp), // 각진 모서리
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("취소", color = Black, fontWeight = FontWeight.Bold)
            }

            // 확인 버튼 (사진처럼 배경색 파란색, 텍스트 흰색)
            Button(
                onClick = {
                    // 선택된 통신사 처리 로직
                },
                enabled = selectedCarrier.isNotBlank(), // 통신사가 선택되어야 활성화
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(0.dp), // 각진 모서리
                colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
            ) {
                Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
    if (showCarrierSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showCarrierSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 목록 영역
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp) // 버튼 영역이 없으므로 하단 여백 추가
                ) {
                    items(carriers) { carrier ->
                        val isSelected = carrier == selectedCarrier
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    selectedCarrier = carrier
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) showCarrierSheet = false
                                    }
                                }
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = carrier,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = if (isSelected) MainBlue else Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}