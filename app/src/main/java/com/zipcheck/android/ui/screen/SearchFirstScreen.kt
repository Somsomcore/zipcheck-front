package com.zipcheck.android.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.TextFieldBorderGray
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    // 텍스트 필드에 입력된 값을 저장하는 상태 변수
    var address by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") } // 상세 주소 추가
    var deposit by remember { mutableStateOf("") }
    var houseType by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    // 모든 필드가 채워졌는지 확인하는 변수
    val allFieldsFilled = address.isNotEmpty() && detailAddress.isNotEmpty() && deposit.isNotEmpty() && houseType.isNotEmpty() && area.isNotEmpty()

    // ⭐ 추가/수정: SearchAddressScreen에서 돌아올 때 주소를 받아오는 로직
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedAddress")?.observe(
            lifecycleOwner
        ) { result ->
            address = result
        }
    }

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
                val houseTypes = listOf("아파트", "다세대", "단독주택", "오피스텔", "빌라", "상가")

                // ✅ 표시용 하이라이트: 선택된 값이 있으면 그 값, 없으면 첫 번째 항목
                val highlighted = houseType ?: houseTypes.first()

                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp), // 너무 길어지지 않도록 최대 높이 설정 (선택 사항)
                    horizontalAlignment = Alignment.CenterHorizontally // LazyColumn 내부 아이템도 중앙 정렬
                ) {
                    items(houseTypes.size) { index ->
                        val type = houseTypes[index]

                        // 선택/비선택 색상
                        val isSelected = highlighted == type
                        val textColor = if (isSelected) MainBlue else TextFieldBorderGray // 회색 톤은 원하는 값으로

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    houseType = type // 선택된 값을 houseType에 저장
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
                text = "필수 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "매물 위험도 평가를 위한 정보를 입력해주세요.",
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
                // ✅ SearchTextField 대신 ClickableTextField 사용
                ClickableTextField(
                    label = "매물 주소",
                    value = address,
                    placeholderText = "지번, 도로명, 건물명으로 검색",
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    onClick = {
                        navController.navigate("search_address")
                    }
                )

                SearchTextField(
                    value = detailAddress,
                    onValueChange = { detailAddress = it },
                    placeholderText = "상세주소를 입력하세요"
                )
            }

            // 거래 희망 보증금
            SearchTextField(
                label = "거래 희망 보증금",
                value = deposit,
                onValueChange = { deposit = it },
                trailingIcon = { Text(text = "만 원", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 매물 종류
//            SearchTextField(
//                label = "매물 종류",
//                value = houseType,
//                onValueChange = { houseType = it },
//                placeholderText = "아파트/다세대",
//                trailingIcon = {
//                    Icon(painterResource(id = R.drawable.ic_dropdown), contentDescription = null, tint = Color.Gray)
//                }
//            )
            ClickableTextField(
                label = "매물 종류",
                value = houseType, // 선택된 값을 표시
                placeholderText = "아파트/다세대",
                leadingIcon = null,
                onClick = {
                    focusManager.clearFocus()
                    showBottomSheet = true // 클릭 시 바텀 시트 표시
                },
                trailingIcon = {
                    Icon(painterResource(
                        id = R.drawable.ic_dropdown),
                        contentDescription = null,
                        tint = Color.Gray)
                }
            )

            // 전용 면적
            SearchTextField(
                label = "전용 면적",
                value = area,
                onValueChange = { area = it },
                trailingIcon = { Text(text = "m²", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // ✅ 하단 버튼을 위로 밀어내는 Spacer
        Spacer(modifier = Modifier.weight(1f))

        // 다음 버튼
        Button(
            onClick = { navController.navigate("search_second") },
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

// 공통된 스타일을 적용한 TextField 컴포저블
@Composable
fun SearchTextField(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "*",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MainBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 14.sp),
            shape = RoundedCornerShape(8.dp), // 둥근 모서리
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            placeholder = { Text(placeholderText, color = PlaceholderGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainBlue,   // ✅ 포커스 시 테두리 색상
                unfocusedBorderColor = TextFieldBorderGray, // ✅ 비포커스 시 테두리 색상
                cursorColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = keyboardOptions,
            singleLine = true,
        )
    }
}

@Composable
fun ClickableTextField(
    label: String? = null,
    value: String, // ⭐ 추가: 표시할 값 (주소)
    placeholderText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 누르는 동안만 파란색, 손 떼면 회색으로 복귀 (부드럽게 애니메이션)
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) MainBlue else TextFieldBorderGray,
        label = "borderColor"
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "*",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MainBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    onClick = {
                    onClick()
                })
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // ⭐ 변경: value가 비어있지 않으면 value를, 비어있으면 placeholderText를 표시
                Text(
                    text = if (value.isNotEmpty()) value else placeholderText,
                    color = if (value.isNotEmpty()) Color.Black else PlaceholderGray, // ⭐ 변경: 값에 따라 글자 색상 변경
                    style = TextStyle(fontSize = 14.sp)
                )

                // This spacer will push the trailing icon to the end.
                Spacer(modifier = Modifier.weight(1f))

                if (trailingIcon != null) {
                    trailingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    ZipcheckfrontTheme {
        val navController = rememberNavController()
        SearchScreen(navController)
    }
}