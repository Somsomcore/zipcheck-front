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
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.component.CustomTopBar
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
fun RegisterScreen(navController: NavHostController) {
    // 텍스트 필드에 입력된 값을 저장하는 상태 변수
    var address by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") } // 상세 주소 추가

    val focusManager = LocalFocusManager.current

    // 모든 필드가 채워졌는지 확인하는 변수
    val allFieldsFilled = address.isNotEmpty() && detailAddress.isNotEmpty()

    // ⭐ 추가/수정: SearchAddressScreen에서 돌아올 때 주소를 받아오는 로직
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedAddress")?.observe(
            lifecycleOwner
        ) { result ->
            address = result
        }
    }

    // 화면 전체를 Column으로 구성
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
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    // `detectTapGestures`를 사용해 탭(터치)이 발생했을 때 키보드를 내립니다.
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
        ) {
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
                    text = "주소를 입력해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "상세주소까지 자세히 입력해주세요.",
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

                // ✅ 하단 버튼을 위로 밀어내는 Spacer
                Spacer(modifier = Modifier.weight(1f))

                // 다음 버튼
                Button(
                    onClick = { if (allFieldsFilled) navController.navigate( "" ) },
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
    }
}