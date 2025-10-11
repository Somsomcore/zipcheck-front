package com.zipcheck.android.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// 재사용 가능한 클릭 가능한 입력 필드 (Text + Icon)
// RegisterInfoScreen에서 사용된 ClickableField를 재사용합니다.
@Composable
fun ClickableField(
    label: String,
    value: String,
    placeholderText: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null, // 아이콘을 위한 파라미터
    trailingIcon: @Composable (() -> Unit)? = null, // 트레일링 아이콘 추가 (파일 첨부 등)
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (isRequired) {
                Text(
                    text = " *",
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, TextFieldBorderGray, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // 아이콘과 텍스트 분리
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    leadingIcon?.invoke() // 리딩 아이콘 렌더링
                    if (leadingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
                    }
                    Text(
                        text = if (value.isNotEmpty()) value else placeholderText,
                        color = if (value.isNotEmpty()) Color.Black else PlaceholderGray,
                        fontSize = 16.sp
                    )
                }
                trailingIcon?.invoke() // 트레일링 아이콘 렌더링
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen3(
    navController: NavHostController,
    // 이전 페이지에서 넘어온 모든 데이터 (API 제출 시 사용)
    // 실제로는 ViewModel이나 다른 상태 관리로 처리해야 하지만, 여기서는 전달받은 것으로 가정합니다.
    previousData: Map<String, String?>
) {
    val focusManager = LocalFocusManager.current

    // 상태 변수
    var content by remember { mutableStateOf("") } // 피해 상황 내용
    var supportingFile by remember { mutableStateOf<String?>(null) } // 근거 자료 (파일 이름)
    var isConsentChecked by remember { mutableStateOf(false) } // 개인정보 수집 및 동의

    // 완료 버튼 활성화 조건: 내용, 근거 자료, 동의 체크 모두 필요
    val isCompleteButtonEnabled = content.isNotEmpty() && supportingFile != null && isConsentChecked

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
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
        ) {
            // LinearProgressIndicator (3/3 진행률)
            LinearProgressIndicator(
                progress = 3f / 3f, // 세 번째 화면이므로 100% 진행률
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = MainBlue,
                trackColor = Gray
            )

            // 섹션 제목
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "피해 상황을 작성해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "계약 과정에서 '뭔가 좀 이상하다'고 느꼈던 순간을 공유해주세요. ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                Text(
                    text = "사소한 대화나 분위기도 다른 사람에겐 중요한 정보가 됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // 1. 내용 입력 (다중 라인 텍스트 필드)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "내용",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " *",
                            color = MainBlue, // 파란색 * 추가
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 200.dp), // 이미지와 유사한 높이로 고정
                        placeholder = {
                            Text(
                                text = "내용내용내용내용...", // 이미지에 표시된Placeholder 텍스트
                                color = PlaceholderGray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainBlue,
                            unfocusedBorderColor = TextFieldBorderGray,
                            cursorColor = MainBlue,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    // 2. 근거 자료 (PDF 첨부 기능)
                    ClickableField(
                        label = "근거 자료",
                        value = supportingFile ?: "",
                        placeholderText = "파일 첨부",
                        isRequired = true,
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.ic_pdf), // 클립 아이콘 (첨부 파일)
                                contentDescription = null,
                                tint = PlaceholderGray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            // TODO: 파일 첨부 로직 호출 (Activity Result API 사용)
                            // 여기서는 파일이 첨부되었다고 가정하고 파일명을 임시로 설정
                            if (supportingFile == null) {
                                supportingFile = "파일제목.pdf"
                            } else {
                                supportingFile = null // 다시 클릭하면 제거되는 것으로 가정
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "첨부하실 수 있는 파일의 종류: 신고 접수증, 등등...",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaceholderGray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                item {
                    // 3. 개인정보 수집 및 동의 체크박스
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isConsentChecked = !isConsentChecked }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // 체크박스 아이콘
                        Icon(
                            painter = painterResource(
                                id = if (isConsentChecked) R.drawable.ic_click_after else R.drawable.ic_click_before
                            ),
                            contentDescription = "개인정보 수집 동의",
                            tint = if (isConsentChecked) Color.Unspecified else Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "개인정보 수집 및 동의",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // 완료 버튼
            Button(
                onClick = {
                    if (isCompleteButtonEnabled) {
                        // 모든 정보를 합쳐서 최종 API 제출을 시뮬레이션합니다.
                        val finalData = previousData.toMutableMap().apply {
                            this["content"] = content
                            this["supportingFile"] = supportingFile
                            this["isConsentChecked"] = isConsentChecked.toString()
                        }
                        println("Final API Submission Data: $finalData")

                        // TODO: 성공 화면 또는 메인 화면으로 이동
                        navController.popBackStack("home_screen", inclusive = false)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 16.dp),
                enabled = isCompleteButtonEnabled, // 활성화 조건 적용
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleteButtonEnabled) MainBlue else Gray,
                    disabledContainerColor = Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "완료",
                    color = if (isCompleteButtonEnabled) White else Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewRegisterDescriptionScreen() {
    ZipcheckfrontTheme {
        // 프리뷰를 위해 임시 NavController와 이전 데이터를 사용
        val dummyPreviousData = mapOf(
            "address" to "서울시 강남구",
            "fraudType" to "아파트",
            "contractDate" to "2024-05-26"
        )
        RegisterScreen3(
            navController = rememberNavController(),
            previousData = dummyPreviousData
        )
    }
}