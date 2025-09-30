package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme // Material3 테마 사용을 위해 임포트 변경
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.contentColorFor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.PurpleGrey40
import com.zipcheck.android.ui.theme.PurpleGrey80

private val MainBlue = Color(0xFF448AFF)
private val LightBlack = Color(0xFF0B0D0E)
private val DarkBlack = Color(0xFF444C55)
private val Gray = Color(0xFFE3E5E8)
private val PlaceholderGray = Color(0xFF757575)
private val SectionGray = Color(0xFFF1F2F4)
private val White = Color(0xFFFFFFFF)


// --- Data Structures ---
data class FraudReport(
    val reporterName: String, // 신고자 이름
    val contractDate: String, // 계약 일자
    val contractType: String, // 계약 형태 (e.g., 전세금)
    val contentPreview: String, // 내용 미리보기
    val reportDate: String // 신고 접수일 (우측 상단 날짜)
)

private val sampleReports = List(4) {
    FraudReport(
        reporterName = "주소지",
        contractDate = "2002.12.12",
        contractType = "전세금",
        contentPreview = "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용...",
        reportDate = "2025.07.31"
    )
}

// --- Composable Functions ---

@Composable
fun FraudRegInquiryScreen(navController: NavHostController) {
    // 0. State for Tab Selection
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("수락 전", "수락 후")

    // 1. Scaffold (Material3) for overall screen structure
    Scaffold(
        topBar = { FraudInquiryTopBar(title = "사기 접수 수락") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SectionGray) // 배경색: SectionGray (F1F2F4)
        ) {
            // 2. Tab Row
            FraudInquiryTabs(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            Spacer(modifier = Modifier.height(8.dp)) // Space below the tabs

            // 3. Total Count Text
            val statusText = if (selectedTabIndex == 0) "총 nn건의 신고가 접수되었어요" else "총 nn건의 신고가 등록되었어요"
            Text(
                text = statusText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                // 폰트 스타일은 Typography의 bodyLarge 또는 커스텀 스타일 사용
                style = androidx.compose.material3.LocalTextStyle.current.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = LightBlack
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 4. Report List
            ReportList(
                reports = sampleReports,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FraudInquiryTopBar(title: String) {
    // Material3 TopAppBar 사용
    TopAppBar(
        title = {
            Text(
                text = title,
                color = LightBlack,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = androidx.compose.material3.LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = {  }) { //navController.popBackStack()
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(48.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White, // 배경색: White
            scrolledContainerColor = White
        )
    )
}

@Composable
fun FraudInquiryTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = White, // 탭 배경색: White
        contentColor = LightBlack,
        indicator = { tabPositions ->
            // 선택된 탭 아래에 MainBlue 색상의 인디케이터 표시
            TabRowDefaults.Indicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(2.dp),
                color = MainBlue // 선택 표시선 색상: MainBlue (448AFF)
            )
        },
        divider = {
            // 탭 아래 얇은 구분선
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) LightBlack else PlaceholderGray // 선택 시 LightBlack, 미선택 시 PlaceholderGray
                    )
                },
            )
        }
    }
}

@Composable
fun ReportList(reports: List<FraudReport>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(reports) { report ->
            ReportItem(report = report)
        }
    }
}

@Composable
fun ReportItem(report: FraudReport) {
    // Material3 Card 사용, elevation 0.dp로 플랫 디자인 유지
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle report item click */ }
            .height(210.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White), // 카드 배경색: White
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: Reporter Name Label and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "신고자 이름",
                    style = androidx.compose.material3.LocalTextStyle.current.copy(
                        color = Black,
                        fontSize = 12.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .background(SectionGray, shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = report.reportDate,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            color = PurpleGrey80,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Reporter Name (Actual Value)
            Text(
                text = report.reporterName,
                style = androidx.compose.material3.LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = LightBlack // 실제 값 색상: LightBlack
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Contract Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "계약 일자",
                        style = androidx.compose.material3.LocalTextStyle.current.copy(color = PlaceholderGray, fontSize = 12.sp)
                    )
                    Text(
                        text = "계약 형태",
                        style = androidx.compose.material3.LocalTextStyle.current.copy(color = PlaceholderGray, fontSize = 12.sp)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = report.contractDate,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold, color = DarkBlack, fontSize = 13.sp)
                    )
                    Text(
                        text = report.contractType,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold, color = DarkBlack, fontSize = 13.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            // Content Preview
            Text(
                text = report.contentPreview,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = androidx.compose.material3.LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold,color = DarkBlack, fontSize = 12.sp)
            )
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun PreviewFraudRegInquiryScreen() {
    // 실제 테마 파일을 적용했다고 가정하고 Preview 실행
    // 여기서는 Material3 기본 테마 내에서 색상을 지정하여 유사하게 구현합니다.
    androidx.compose.material3.MaterialTheme {
        FraudRegInquiryScreen(navController = rememberNavController())
    }
}