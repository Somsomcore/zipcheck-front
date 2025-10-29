package com.zipcheck.android.ui.screen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
// import com.google.android.gms.common.api.Response // 이 임포트도 Retrofit의 Response와 충돌할 수 있으므로 제거
import com.zipcheck.android.R
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.network.RetrofitClient
import com.zipcheck.android.ui.network.VerificationCodeRequest
import com.zipcheck.android.ui.network.VerificationCodeResponse
import com.zipcheck.android.ui.network.VerifyCodeRequest
import com.zipcheck.android.ui.network.VerifyCodeResponse
import com.zipcheck.android.ui.theme.PurpleGrey80
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// 1. Retrofit의 Call과 Response를 명시적으로 임포트
import retrofit2.Call
import retrofit2.Response
import kotlin.math.abs
import kotlin.text.find
import kotlin.text.toFloat
import kotlin.time.Duration.Companion.seconds

// 🎨 색상 정의 (사용자 이미지 기반)
val MainBlue = Color(0xFF4285F4) // 파란색 버튼 및 포커스 색상
val Black = Color(0xFF000000)
val TextFieldBorderGray = Color(0xFFD0D0D0)
val LightGrayBackground = Color(0xFFF0F0F0) // 모달 배경 등
val TimeGray = Color(0xFF888888) // 인증번호 타이머 색상

// 네비게이션 더미 함수 (실제 환경에서는 NavController 사용)
fun NavController.popBackStackAndShowToast(message: String) {
    println("Navigate back to LoginScreen. Show Toast: $message")
}

// ⏳ 인증번호 타이머 Composable
@Composable
fun AuthTimer(
    modifier: Modifier = Modifier,
    initialDurationSeconds: Int = 180, // 3분
    onTimeout: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(initialDurationSeconds) }

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1.seconds)
            timeLeft--
        } else {
            onTimeout()
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Text(
        text = timeText,
        color = TimeGray,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}

// 📱 회원가입 메인 화면
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputScreen(navController: NavController) {
    val carriers = remember { listOf("SKT", "KT", "LG U+", "SKT 알뜰폰", "KT 알뜰폰", "LG U+ 알뜰폰", "SKT") }

    // 🌟 상태 변수
    var name by remember { mutableStateOf("") } // 첫번째 그림의 예시값
    var selectedCarrier by remember { mutableStateOf("") } // 첫번째 그림의 예시값
    var phoneNumber by remember { mutableStateOf("") } // 첫번째 그림의 예시값
    var isCarrierSheetVisible by remember { mutableStateOf(false) }
    var isAuthSheetVisible by remember { mutableStateOf(false) }
    var isNameFocused by remember { mutableStateOf(false) }
    var isCarrierFocused by remember { mutableStateOf(false) }
    var isPhoneFocused by remember { mutableStateOf(false) }
    var phoneAuthState by remember { mutableStateOf<PhoneAuthState>(PhoneAuthState.Pending) } // 인증 상태
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 팝업 표시 함수
    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        topBar = { CustomTopBar("", navController as NavHostController) },
        containerColor = Color.White,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                // 이미지의 팝업처럼 화면 하단 중앙에 나타나도록 커스텀
                Snackbar(
                    modifier = Modifier.padding(bottom = 20.dp),
                    containerColor = Black.copy(alpha = 0.8f),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(data.visuals.message, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        bottomBar = {
            // "완료" 버튼 영역 (인증 완료 상태일 때만 표시)
            AnimatedVisibility(
                visible = phoneAuthState is PhoneAuthState.Completed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("signup_result_key", "success") // "성공했다"는 약속된 데이터를 저장
                        navController.popBackStack() // 이전 화면으로 돌아가기
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                ) {
                    Text("완료", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            // "확인" 버튼 영역 (통신사 선택 모달에 사용됨)
            // 메인 화면에는 버튼이 없고, 인증 완료 시 "완료" 버튼이 나타남.
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            // 1. 메인 콘텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // 제목
                Text(
                    text = "계정 생성을 위해\n아래 정보를 입력해 주세요",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 16.dp),
                    color = Black
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 1. 이름 입력 필드
                Text(text = "이름", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .onFocusChanged { isNameFocused = it.isFocused },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        // 1. 입력 시 테두리 파란색
                        focusedBorderColor = MainBlue,
                        unfocusedBorderColor = if (isNameFocused) MainBlue else TextFieldBorderGray,
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 2. 통신사 선택 필드
                Text(
                    text = "통신사",
                    style = MaterialTheme.typography.titleMedium,
                    color = Black
                )
                OutlinedTextField(
                    value = selectedCarrier,
                    onValueChange = { /* 텍스트 입력 방지 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusManager.clearFocus() // 다른 포커스 해제
                            isCarrierSheetVisible = true // 바텀 시트 표시
                        },
                    readOnly = true,
                    enabled = false,
                    singleLine = true,
                    trailingIcon = {
                        Image(painter = painterResource(id = R.drawable.icon_down), // 여기에 원하는 아이콘 리소스 ID를 넣으세요.
                            contentDescription = "통신사 선택", // 아이콘에 대한 설명 (접근성을 위함)
                            modifier = Modifier.size(16.dp) // 아이콘 크기 조절
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        // 1. 입력 시 테두리 파란색 (클릭으로 처리되므로 unfocused에도 반영)
                        focusedBorderColor = MainBlue,
                        unfocusedBorderColor = if (isCarrierFocused) MainBlue else TextFieldBorderGray,
                        disabledBorderColor = TextFieldBorderGray // 클릭으로 인한 활성화/비활성화 시
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 3. 휴대폰 번호 입력 필드
                Text(text = "휴대폰 번호", style = MaterialTheme.typography.titleMedium,
                    color = Black)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 11) phoneNumber = it }, // 11자리 제한
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .onFocusChanged { isPhoneFocused = it.isFocused },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        // 1. 입력 시 테두리 파란색
                        focusedBorderColor = MainBlue,
                        unfocusedBorderColor = if (isPhoneFocused) MainBlue else TextFieldBorderGray,
                    ),
                    // 👇 trailingIcon을 사용하여 버튼을 내부에 배치합니다.
                    trailingIcon = {
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (phoneNumber.isBlank()) {
                                    showSnackbar("휴대폰 번호가 입력되지 않았습니다.")
                                } else {
                                    // 📡 인증번호 요청 API 호출
                                    val request = VerificationCodeRequest(phone = phoneNumber)
                                    RetrofitClient.authService.sendVerificationCode(request)
                                        .enqueue(object :
                                            retrofit2.Callback<VerificationCodeResponse> {
                                            override fun onResponse(
                                                // 1. retrofit2.Call 사용
                                                call: Call<VerificationCodeResponse>,
                                                response: Response<VerificationCodeResponse>
                                            ) {
                                                if (response.isSuccessful && response.body()?.isSuccess == true) {
                                                    showSnackbar("인증번호가 발송되었습니다.")
                                                    isAuthSheetVisible = true
                                                } else {
                                                    showSnackbar("문자 발송 실패: ${response.body()?.message ?: response.message()}")
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<VerificationCodeResponse>,
                                                t: Throwable
                                            ) {
                                                showSnackbar("네트워크 오류: ${t.message}")
                                            }
                                        })
                                }
                            },
                            // 인증 완료 상태일 경우 버튼 모양 변경
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (phoneAuthState) {
                                    is PhoneAuthState.Completed -> Color(0xFFE0F7FA) // 연한 색상
                                    // 이미지의 회색 배경과 유사하게
                                    else -> Color(0xFFF1F2F4) // SectionGray 또는 유사한 회색
                                },
                                disabledContainerColor = Color(0xFFF1F2F4) // 비활성화 시 색상
                            ),
                            shape = RoundedCornerShape(8.dp), // 이미지처럼 모서리를 둥글게
                            modifier = Modifier
                                .height(36.dp) // 버튼 높이를 텍스트 필드보다 작게 조절
                                .padding(end = 8.dp) // 우측 여백
                        ) {
                            Text(
                                text = when (phoneAuthState) {
                                    is PhoneAuthState.Completed -> "인증 완료"
                                    else -> "인증 요청"
                                },
                                color = when (phoneAuthState) {
                                    is PhoneAuthState.Completed -> MainBlue
                                    // 이미지의 글자색과 유사하게
                                    else -> Color(0xFF8B96A2) // PdfGrey 또는 유사한 회색
                                },
                                fontSize = 12.sp // 글자 크기를 작게 조절
                            )
                        }
                    }
                )
            }
        }
    }

    // 2. 통신사 선택 ModalBottomSheet
    if (isCarrierSheetVisible) {
        CarrierSelectionSheet(
            sheetState = sheetState,
            carriers = carriers,
            selectedCarrier = selectedCarrier,
            onDismiss = { isCarrierSheetVisible = false },
            onCarrierConfirmed = { newCarrier ->
                selectedCarrier = newCarrier
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) isCarrierSheetVisible = false
                }
            }
        )
    }

    // 4. 휴대폰 인증번호 입력 ModalBottomSheet
    if (isAuthSheetVisible) {
        AuthNumberInputSheet(
            sheetState = sheetState,
            onDismiss = { isAuthSheetVisible = false },
            onAuthCompleted = {
                phoneAuthState = PhoneAuthState.Completed(true)
                showSnackbar("인증이 완료되었습니다.") // 인증 완료 스낵바 추가
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) isAuthSheetVisible = false
                }
            },
            // 5. onAuthFailed가 메시지를 받도록 수정
            onAuthFailed = { message ->
                // 5. 인증번호 틀릴 시 팝업
                showSnackbar(message)
            },
            onResend = {
                // "다시 보내기" 로직 (타이머 리셋 등)
                println("인증번호 다시 보내기 요청")
                // TODO: 여기에 실제 재전송 API 호출 로직을 구현
                showSnackbar("인증번호를 다시 전송합니다.")
            }
        )
    }
}

// ✅ 2. 통신사 선택 ModalBottomSheet Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierSelectionSheet(
    sheetState: SheetState,
    carriers: List<String>,
    selectedCarrier: String,
    onDismiss: () -> Unit,
    onCarrierConfirmed: (String) -> Unit
) {
    // 1. 초기 스크롤 위치 설정을 위한 상태 (선택된 항목이 없으면 0)
    val initialIndex = carriers.indexOf(selectedCarrier).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // 2. 현재 선택된 항목을 임시로 저장
    var tempSelectedCarrier by remember { mutableStateOf(selectedCarrier) }
    val scope = rememberCoroutineScope()
    var currentCenteredCarrier by remember { mutableStateOf(selectedCarrier) }

    // 3. 스크롤이 멈췄을 때 중앙 항목을 선택된 것으로 처리
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val viewportCenter = listState.layoutInfo.viewportSize.height / 2
                val centerItem = visibleItems.minByOrNull {
                    abs((it.offset + it.size / 2) - viewportCenter)
                }
                centerItem?.let {
                    if (it.index < carriers.size) {
                        currentCenteredCarrier = carriers[it.index] // 스크롤 멈추면 중앙 항목을 임시 선택
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null, // 핸들 제거하여 이미지와 유사하게
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            val listHeight = 300.dp
            val itemHeight = 56.dp
            val verticalPadding = (listHeight / 2) - (itemHeight / 2)

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(listHeight),
                // 상단과 하단에 패딩을 주어 첫 아이템과 마지막 아이템이 중앙에 올 수 있도록 함
                contentPadding = PaddingValues(vertical = verticalPadding)
            ) {
                itemsIndexed(carriers) { index, carrier ->
                    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                    val currentItemInfo = visibleItemsInfo.find { it.index == index }

                    // 3. 중앙 아이템 계산 로직 수정 (현재 중앙에 가장 가까운 아이템 인덱스)
                    val centeredIndex = currentItemInfo?.let {
                        val viewportCenter = listState.layoutInfo.viewportSize.height / 2
                        visibleItemsInfo.minByOrNull { item -> abs((item.offset + item.size / 2) - viewportCenter) }?.index
                    }
                    val isSelected = (centeredIndex == index)

                    // 중앙으로부터의 거리를 계산하여 그래픽 효과 적용
                    val (scale, alpha) = currentItemInfo?.let {
                        val viewportCenter = listState.layoutInfo.viewportSize.height / 2
                        val itemCenter = it.offset + it.size / 2
                        val distance = abs(viewportCenter - itemCenter).toFloat()
                        // 거리에 따른 스케일과 알파값 계산 (중앙일수록 1.0, 멀수록 작아짐)
                        val maxDistance = viewportCenter.toFloat().coerceAtLeast(1f) // 0으로 나누기 방지
                        // 거리가 뷰포트 절반을 넘어가면 효과를 최대치로 적용
                        val normalizedDistance = (distance / maxDistance).coerceAtMost(1f)

                        val scale = (1f - (normalizedDistance * 0.4f)).coerceIn(0.6f, 1.0f) // 중앙 1.0, 가장자리 0.6
                        val alpha = (1f - (normalizedDistance * 0.7f)).coerceIn(0.3f, 1.0f) // 중앙 1.0, 가장자리 0.3
                        scale to alpha
                    } ?: (0.6f to 0.3f) // 화면에 안 보이는 항목의 기본값

                    Text(
                        text = carrier,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (isSelected) MainBlue else Black, // 선택된 항목은 파란색
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 30.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .graphicsLayer { // graphicsLayer를 사용하여 스케일과 알파 적용
                                this.scaleX = scale
                                this.scaleY = scale
                                this.alpha = alpha
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // 클릭 시 해당 항목으로 스크롤
                                scope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            }
                            // Text에 수직 정렬을 위한 wrapper 추가
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            }


            // 하단 버튼 (취소/확인)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                // 취소 버튼
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(0.5.dp, TextFieldBorderGray)
                ) {
                    Text("취소", color = Black, fontWeight = FontWeight.Bold)
                }

                // 2. 확인 버튼 (잘못된 인증 로직 제거)
                Button(
                    onClick = {
                        // 3. 스크롤이 멈췄을 때 중앙에 있던 항목(currentCenteredCarrier)을 콜백으로 전달
                        onCarrierConfirmed(currentCenteredCarrier)
                    },
                    // 2. modifier, shape, enabled 수정
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(0.dp), // '취소'와 통일
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                    enabled = true // 항상 활성화
                ) {
                    Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                }

            }
        }
    }
}


// 🔑 4. 휴대폰 인증번호 입력 ModalBottomSheet Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthNumberInputSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAuthCompleted: () -> Unit,
    onAuthFailed: (message: String) -> Unit, // 5. 실패 메시지를 전달받도록 수정
    onResend: () -> Unit
) {
    // 3. authNumber, isTimeout 변수 선언 (오류 해결)
    var authNumber by remember { mutableStateOf("") }
    var isAuthNumberFocused by remember { mutableStateOf(false) }
    var isTimeout by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(2.dp))
            )
        },
        containerColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 본문: 스크롤 + 하단 버튼 공간만큼 여유 패딩
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 120.dp) // 버튼 높이 + 여유
                    .imePadding() // 키보드 올라올 때 안전
            ) {
                Text(
                    text = "문자로 전송된\n인증번호를 입력해주세요",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 30.dp)
                )

                // 입력 박스 ...
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (isAuthNumberFocused) MainBlue else TextFieldBorderGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(4.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = authNumber,
                        onValueChange = { if (it.length <= 6) authNumber = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .onFocusChanged { isAuthNumberFocused = it.isFocused }
                    ) { innerTextField ->
                        if (authNumber.isEmpty()) {
                            Text(text = "인증번호 6자리", color = TextFieldBorderGray)
                        }
                        innerTextField()
                    }

                    if (!isTimeout) {
                        AuthTimer(
                            modifier = Modifier.padding(end = 16.dp),
                            onTimeout = { isTimeout = true }
                        )
                    } else {
                        Text(
                            text = "00:00",
                            color = TimeGray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "인증 문자를 받지 못하셨나요? ",
                        color = TimeGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "다시 보내기",
                        color = Black,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable {
                            onResend()
                            isTimeout = false
                        }
                    )
                }
            }

            // 하단 고정 확인 버튼
            Surface(
                color = Color.White,
                tonalElevation = 2.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding() // ✅ 제스처바 영역 고려
                    .imePadding()            // ✅ 키보드가 올라올 때 밀어올림
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp) // Defined padding around button
                ) {
                    Button(
                        onClick = {
                            val request = VerifyCodeRequest(authNumber)
                            RetrofitClient.authService.verifyCode(request)
                                .enqueue(object : retrofit2.Callback<VerifyCodeResponse> {
                                    override fun onResponse(
                                        call: Call<VerifyCodeResponse>, // 1. retrofit2.Call
                                        response: Response<VerifyCodeResponse> // 1. retrofit2.Response
                                    ) {
                                        // 4. VerifyCodeGResponse -> VerifyCodeResponse
                                        if (response.isSuccessful && response.body()?.isSuccess == true) {
                                            onAuthCompleted() // 3. 성공 콜백 호출
                                        } else {
                                            // 3. 실패 콜백 호출 (메시지 전달)
                                            onAuthFailed("잘못된 인증번호입니다. 다시 입력해주세요.")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<VerifyCodeResponse>,
                                        t: Throwable
                                    ) {
                                        // 3. 실패 콜백 호출 (메시지 전달)
                                        onAuthFailed("네트워크 오류: ${t.message}")
                                    }
                                })
                        },
                        // 3. authNumber, isTimeout 참조 오류 해결
                        enabled = authNumber.length == 6 && !isTimeout,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // Button height is now fixed and correctly padded by the Column
                    ) {
                        // FIX: Changed text color to White for better contrast
                        Text("확인", color = Color.White, fontWeight = FontWeight.Bold) // 검은색 -> 흰색
                    }
                }
            }
        }
    }
}

// 📱 인증 상태 Enum
sealed class PhoneAuthState {
    data object Pending : PhoneAuthState()
    data class Completed(val success: Boolean) : PhoneAuthState()
}

// 🚀 미리보기
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    NameInputScreen(rememberNavController())
}