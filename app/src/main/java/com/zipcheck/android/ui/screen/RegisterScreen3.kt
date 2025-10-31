package com.zipcheck.android.ui.screen

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.copy
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import com.zipcheck.android.data.model.report.ReportViewModel
import com.zipcheck.android.data.model.report.SubmitState
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.TextFieldBorderGray
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



// 재사용 가능한 클릭 가능한 입력 필드 (Text + Icon)
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
                    color = MainBlue,
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    leadingIcon?.invoke()
                    if (leadingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (value.isNotEmpty()) value else placeholderText,
                        color = if (value.isNotEmpty()) Color.Black else PlaceholderGray,
                        fontSize = 16.sp
                    )
                }
                trailingIcon?.invoke()
            }
        }
    }
}
private const val TAG = "RegisterScreen3"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen3(
    navController: NavHostController,
    reportVm: ReportViewModel
) {
    Log.d(TAG, "Composable ENTER RegisterScreen3")
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // ViewModel 제출 상태 구독
    val submitState by reportVm.submitState.collectAsStateWithLifecycle()

    // 로컬 UI 상태
    var content by remember { mutableStateOf("") }            // 피해 상황 내용
    var isConsentChecked by remember { mutableStateOf(false) } // 개인정보 동의
    var fileName by remember { mutableStateOf<String?>(null) } // 첨부 파일명 표시용

    // 파일 선택 런처 (PDF만)
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        Log.d(TAG, "PDF picker result: uri=$uri")
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                Log.d(TAG, "takePersistableUriPermission success") // ★ 로그
            } catch (e: SecurityException) {
                Log.e(TAG, "takePersistableUriPermission failed: ${e.message}", e) // ★ 로그
            }

            // VM에 증빙 파일 저장
            reportVm.setEvidence(uri)
            fileName = uri.lastPathSegment ?: "evidence.pdf"
            Log.d(TAG, "evidence set in VM, fileName=$fileName") // ★ 로그
        }
    }
    // 내용 입력이 바뀔 때마다 VM에 반영
    LaunchedEffect(content) {
        Log.d(TAG, "content updated: length=${content.length}")
        reportVm.setContent(content)
    }

    LaunchedEffect(isConsentChecked) {
        Log.d(TAG, "consent changed: $isConsentChecked") // ★ 로그
    }

    // 완료 버튼 활성화 조건
    val isComplete = content.isNotEmpty() && fileName != null && isConsentChecked

    LaunchedEffect(Unit) {
        snapshotFlow { isComplete }
            .distinctUntilChanged()
            .collect { now ->
                Log.d(TAG, "isComplete changed: $now (contentNotEmpty=${content.isNotEmpty()}, fileName=${fileName != null}, consent=$isConsentChecked)") // ★ 로그
            }
    }


    // 제출 성공 시 완료 화면으로 이동
    LaunchedEffect(submitState) {
        Log.d(TAG, "submitState changed: $submitState")
        if (submitState is SubmitState.Success) {
            Log.d(TAG, "submitState is Success → navigate to register_screen_4")
            navController.navigate("register_screen_4") {
                // 등록 플로우 앞단까지 스택 정리하고 싶으면 필요에 맞게 수정
                // popUpTo("register_graph") { inclusive = false }
                launchSingleTop = true
            }
            Log.d(TAG, "navigate() issued")
        }
    }

    Scaffold(
        containerColor = White,
        topBar = { CustomTopBar("사기 등록", navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
        ) {
            // 진행률
            LinearProgressIndicator(
                progress = 3f / 3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = MainBlue,
                trackColor = Gray
            )

            // 타이틀
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "피해 상황을 작성해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "계약 과정에서 '뭔가 좀 이상하다'고 느꼈던 순간을 공유해주세요.",
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

            // 폼
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // 내용
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "내용",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " *",
                            color = MainBlue,
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
                            .heightIn(min = 200.dp, max = 200.dp),
                        placeholder = { Text("내용내용내용내용...", color = PlaceholderGray) },
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
                    // 근거 자료 (PDF 첨부)
                    ClickableField(
                        label = "근거 자료",
                        value = fileName ?: "",
                        placeholderText = "PDF 파일 첨부",
                        isRequired = true,
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.ic_pdf),
                                contentDescription = null,
                                tint = PlaceholderGray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            // PDF만 선택
                            pdfPicker.launch(arrayOf("application/pdf"))
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "첨부 가능한 파일: 신고 접수증 등 (PDF 권장)",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaceholderGray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                item {
                    // 개인정보 동의
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isConsentChecked = !isConsentChecked }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
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

                // 제출 상태 안내 (옵션)
                item {
                    when (val s = submitState) {
                        is SubmitState.Idle -> {}
                        is SubmitState.Loading -> {
                            Text("제출 중...", color = MainBlue)
                        }
                        is SubmitState.Error -> {
                            Text(s.message, color = Color.Red)
                        }
                        is SubmitState.Success -> {
                            // 성공 이동은 LaunchedEffect에서 처리
                        }
                    }
                }
            }

            // 완료 버튼
            Button(
                onClick = {
                    navController.navigate("register_screen_4") {
                        launchSingleTop = true }},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 16.dp),
                enabled = isComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isComplete) MainBlue else Gray,
                    disabledContainerColor = Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "완료",
                    color = if (isComplete) White else Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}