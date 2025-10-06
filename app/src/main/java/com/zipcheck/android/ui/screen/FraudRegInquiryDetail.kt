package com.zipcheck.android.ui.screen

import android.app.DownloadManager
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import java.util.Locale
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.compose.ui.draw.clip
import com.zipcheck.android.ui.theme.Black


// ⚠️ 이전 파일에서 정의된 색상 및 스타일을 다시 정의하거나 임포트해야 합니다.
private val MainBlue = Color(0xFF448AFF)
private val LightBlack = Color(0xFF0B0D0E)
private val DarkBlack = Color(0xFF444C55)
private val Gray = Color(0xFFE3E5E8)
private val PlaceholderGray = Color(0xFF757575)
private val SectionGray = Color(0xFFF1F2F4)
private val White = Color(0xFFFFFFFF)
private val PdfGrey = Color(0xFF8B96A2)

// --- Data Structure for Detail Screen ---
data class FraudDetail(
    val addressRoad: String,
    val addressDetail: String,
    val reportStatus: String, // 예: "강제 전세"
    val reporterName: String,
    val reportDate: String, // 신고 접수일
    val contractType: String,
    val contractDate: String,
    val fraudAwareDate: String, // 사기 인지 일자
    val content: String,
    val evidenceFile: String // 근거 자료 파일 이름
)

// 더미 데이터 (이미지와 동일한 내용)
private val sampleDetail = FraudDetail(
    addressRoad = "경기도 구리시 경춘로 276번길 34",
    addressDetail = "힐스테이트 구리역 102동 1903호",
    reportStatus = "깡통전세",
    reporterName = "신고자 이름",
    reportDate = "2025.07.31",
    contractType = "전세금",
    contractDate = "2002.12.12",
    fraudAwareDate = "2002.12.12",
    content = "내용내용내용내용내용내용내용",
    evidenceFile = ""
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FraudRegInquiryDetailScreen(
    navController: NavHostController,
    detail: FraudDetail = sampleDetail
) {
    // 바텀시트 상태
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(true) }
    // 상태 값
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showReasonSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DetailTopBar(navController) } // ✅ 상단 앱바만 Scaffold에 둠
    ) { paddingValues ->

        // 지도를 화면에 표시
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
//            MapSection(detail)

            // 상세 정보 + 버튼을 바텀시트로
            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = White,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AddressSection(detail)

                        Spacer(Modifier.height(10.dp))

                        ReportInfoSection(detail)

                        Spacer(Modifier.height(10.dp))

                        ContentSection(detail)

                        Spacer(Modifier.height(24.dp))

                        EvidenceSection(detail)

                        Spacer(Modifier.height(24.dp))

                        BottomActionButtons(
                            onDenyClick = { /* 반려 로직 */ },
                            onAcceptClick = { showConfirmDialog = true }
                        )
                    }
                }
                if (showConfirmDialog) {
                    BasicAlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        modifier = Modifier.width(300.dp)
                    ) {
                        Surface(
                            // [수정 1] 버튼을 꽉 채우기 위해 하단 모서리를 0.dp로 설정
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            color = White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // 1. 텍스트 영역 (팝업의 콘텐츠)
                                Text(
                                    text = "정말 수락 하시겠습니까?",
                                    color = Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp, bottom = 40.dp, start = 24.dp, end = 24.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(top = 1.dp), // 버튼 윗줄에 얇은 구분선처럼 보이도록
                                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    // 취소 버튼
                                    Button(
                                        onClick = { showConfirmDialog = false },
                                        // 버튼 shape는 0.dp 유지 (Surface 하단 모서리와 일치)
                                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black // 텍스트 색상을 검정색 계열로 변경 (이미지 반영)
                                        ),
                                        border = BorderStroke(0.5.dp, Color.LightGray), // 중앙 구분선 역할
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    ) {
                                        Text("취소", color = Color.Black)
                                    }

                                    // 확인 버튼
                                    Button(
                                        onClick = {
                                            showConfirmDialog = false
                                            showReasonSheet = true
                                        },
                                        // 버튼 shape는 0.dp 유지
                                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MainBlue,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    ) {
                                        Text("확인", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                if (showReasonSheet) {
                    BasicAlertDialog(
                        onDismissRequest = { showReasonSheet = false },
                        modifier = Modifier.width(300.dp)
                    ) {
                        Surface(
                            // [Shape 수정] 다이얼로그 하단 모서리를 0.dp로 설정하여 버튼이 꽉 붙도록 합니다.
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            color = White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {

                                // 1. Title 영역
                                Text(
                                    text = "반려 사유 선택",
                                    color = Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp, bottom = 12.dp)
                                        .padding(horizontal = 24.dp), // 좌우 패딩 추가
                                    textAlign = TextAlign.Center
                                )

                                // 2. Radio Button List 영역 (스크롤 가능)
                                val reasons = listOf(
                                    "근거 자료 신뢰성 및 타당성 미흡", "거짓정보 포함", "욕설/비하/혐오/차별적 표현",
                                    "스팸홍보/도배", "불법정보 포함", "유출/사칭/사기"
                                )
                                var selectedReason by remember { mutableStateOf(reasons[0]) }

                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 24.dp) // 좌우 패딩 추가
                                ) {
                                    reasons.forEachIndexed { index, reason ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedReason = reason }
                                                .padding(vertical = 12.dp) // 패딩 조정
                                        ) {
                                            // [수정 2 & 3] RadioButton 대신 Icon 사용
                                            val isSelected = selectedReason == reason
                                            Icon(
                                                painter = painterResource(
                                                    id = if (isSelected) R.drawable.ic_fraud_select else R.drawable.ic_fraud
                                                ),
                                                contentDescription = if (isSelected) "Selected" else "Unselected",
                                                tint = if (isSelected) Color.Unspecified else Color.LightGray,
                                                modifier = Modifier.size(16.dp)
                                            )

                                            Spacer(Modifier.width(16.dp)) // 아이콘과 텍스트 사이 간격

                                            Text(
                                                reason,
                                                color = if (isSelected) MainBlue else Black,
                                                fontWeight = FontWeight.Normal, // 선택된 항목 굵게
                                                modifier = Modifier.padding(end = 8.dp) // 오른쪽 여백
                                            )
                                        }

                                        // [수정 2] Divider 추가 (마지막 항목에는 제외)
                                        if (index < reasons.size - 1) {
                                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                                        }
                                    }
                                } // Radio Button List Column 닫기

                                // 3. Button 영역 (하단 꽉 채우기)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(top = 1.dp), // 버튼 위에 얇은 구분선처럼 보이도록
                                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    // 취소 버튼 (왼쪽)
                                    Button(
                                        onClick = { showReasonSheet = false },
                                        // [수정 1] 상단 모서리 0.dp, 하단 모서리도 0.dp로 설정하여 꽉 채웁니다.
                                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black
                                        ),
                                        // [Border] 취소 버튼의 오른쪽 경계선만 추가 (Divider 역할)
                                        border = BorderStroke(0.5.dp, Color.LightGray),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    ) {
                                        Text("취소", color = Color.Black)
                                    }

                                    // 확인 버튼 (오른쪽)
                                    Button(
                                        onClick = {
                                            showReasonSheet = false
                                            navController.navigate("fraudRegInquiry?showPopup=true") {
                                                launchSingleTop = true
                                            }
                                        },
                                        // [수정 1] 상단 모서리 0.dp, 하단 모서리도 0.dp로 설정하여 꽉 채웁니다.
                                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MainBlue,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    ) {
                                        Text("확인", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Composable Sub-Functions ---

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DetailTopBar(navController: NavHostController) {
        TopAppBar(
            title = {
                Text(
                    text = "사기 접수 수락",
                    color = LightBlack,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            actions = {
                Spacer(modifier = Modifier.width(48.dp)) // 중앙 정렬 균형 맞추기
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = White,
                scrolledContainerColor = White
            )
        )
    }

@Composable
fun MapSection(detail: FraudDetail) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    // Geocoding → LatLng 변환
    var latLng by remember { mutableStateOf(LatLng(37.5665, 126.9780)) } // 기본값 서울
    LaunchedEffect(detail.addressRoad) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val results = geocoder.getFromLocationName(detail.addressRoad, 1)
        if (!results.isNullOrEmpty()) {
            latLng = LatLng(results[0].latitude, results[0].longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = latLng),
            title = detail.addressRoad,
            snippet = detail.addressDetail,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
    }
}


@Composable
fun AddressSection(detail: FraudDetail) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(verticalAlignment = Alignment.Top) { // Alignment.Top으로 변경하여 주소 Column이 위로 정렬되도록 함
            // 상태 칩 (예: 강제전세)
            Text(
                text = detail.reportStatus,
                color = White,
                modifier = Modifier
                    .background(MainBlue, RoundedCornerShape(8.dp)) // 파란색 배경
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(10.dp))

            // 도로명 주소와 상세 주소를 담는 Column
            Column {
                // 도로명 주소
                Text(
                    text = detail.addressRoad,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LightBlack
                    )
                )
                // Spacer(Modifier.height(4.dp)) // 필요에 따라 주소 사이 간격 조절
                // 상세 주소
                Text(
                    text = detail.addressDetail,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PlaceholderGray,
                        fontSize = 14.sp
                    )
                    // modifier = Modifier.padding(top = 4.dp) // Text 자체에 top padding을 줄 수도 있음
                )
            }
        }
        // Spacer(Modifier.height(4.dp)) // 이 Spacer는 이제 Column 내부로 이동하거나, 필요 없을 수 있습니다.
    }
}


@Composable
fun ReportInfoSection(detail: FraudDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 신고자 이름 + 신고일자 칩
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = detail.reporterName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PlaceholderGray
                )
            )
            Box(
                modifier = Modifier.background(Gray, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = detail.reportDate,
                    style = LocalTextStyle.current.copy(
                        color = PlaceholderGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        ReportInfoRow(label = "계약 형태", value = detail.contractType)
        Spacer(Modifier.height(16.dp))
        ReportInfoRow(label = "계약 일자", value = detail.contractDate)
        Spacer(Modifier.height(16.dp))
        ReportInfoRow(label = "사기 인지 일자", value = detail.fraudAwareDate)
    }
}

@Composable
fun ReportInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = PlaceholderGray))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(color = LightBlack, fontWeight = FontWeight.SemiBold))
    }
}


    @Composable
    fun InfoLabelAndValue(label: String, value: String) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PlaceholderGray,
                    fontSize = 12.sp
                )
            )
        }
    }


    @Composable
    fun ContentSection(detail: FraudDetail) {
        Column(modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = SectionGray), // 연한 회색 배경
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    text = detail.content,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkBlack
                    )
                )
            }
        }
    }

@Composable
fun EvidenceSection(detail: FraudDetail) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "근거 자료",
            style = MaterialTheme.typography.bodySmall.copy(color = PlaceholderGray)
        )
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable {
                    val url = "https://your-server.com/files/${detail.evidenceFile}"
                    val request = DownloadManager.Request(Uri.parse(url))
                        .setTitle(detail.evidenceFile)
                        .setDescription("PDF 다운로드 중…")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, detail.evidenceFile)

                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                },
            colors = CardDefaults.cardColors(containerColor = PdfGrey),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (detail.evidenceFile.isNotEmpty()) detail.evidenceFile else "첨부된 PDF 없음",
                    style = MaterialTheme.typography.bodyMedium.copy(color = White)
                )
                Icon(
                    painter = painterResource(id = R.drawable.icon_download),
                    contentDescription = "다운로드",
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
    fun BottomActionButtons(onDenyClick: () -> Unit, onAcceptClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding()
        ) {
            // 반려하기 버튼 (좌측)
            OutlinedButton(
                onClick = onDenyClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp, bottomStart = 16.dp, // 왼쪽 모서리 0dp
                    topEnd = 16.dp, bottomEnd = 16.dp
                ),
                colors = ButtonDefaults.buttonColors(containerColor = White,       // 배경 흰색
                    contentColor = LightBlack     // 글자색
                ),
            ) {
                Text("반려 하기", fontWeight = FontWeight.SemiBold)
            }

            // 수락하기 버튼 (우측)
            Button(
                onClick = onAcceptClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp, bottomStart = 16.dp, // 왼쪽 모서리 0dp
                    topEnd = 16.dp, bottomEnd = 16.dp
                ),
                colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
            ) {
                Text("수락 하기", color = White, fontWeight = FontWeight.SemiBold)
            }
        }
    }

// --- Preview ---
    @Preview(showBackground = true)
    @Composable
    fun PreviewFraudRegInquiryDetailScreen() {
        MaterialTheme {
            FraudRegInquiryDetailScreen(navController = rememberNavController(), detail = sampleDetail)
        }
    }
