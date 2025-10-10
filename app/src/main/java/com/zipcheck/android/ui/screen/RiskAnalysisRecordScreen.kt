package com.zipcheck.android.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zipcheck.android.R
import com.zipcheck.android.data.RiskAnalysis.AnalysisGroup
import com.zipcheck.android.data.RiskAnalysis.groupedResults
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.component.MonthYearPicker
import com.zipcheck.android.ui.component.RiskResultCard
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.White
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskAnalysisRecordScreen(navController: NavHostController) {
    // 월 선택 바텀 시트 상태 관리
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf(LocalDate.of(2025, 9, 1)) }

    // 현재 표시할 데이터 필터링 (선택된 월)
    val currentMonthResults = groupedResults.filter {
        it.date.year == selectedMonth.year && it.date.month == selectedMonth.month
    }

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("위험도 분석 기록", navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── 1. 월 선택 토글 (Header) ──
            MonthToggleHeader(
                selectedMonth = selectedMonth,
                onClick = { showBottomSheet = true }
            )

            // ── 2. 날짜별 분석 기록 목록 ──
            AnalysisRecordList(
                currentMonthResults,
                navController = navController
            )
        }
    }

    // ── 3. 월 선택 바텀 시트 표시 ──
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            MonthYearPicker(
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                },
                onConfirm = { year, month ->
                    selectedMonth = LocalDate.of(year, month, 1)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthToggleHeader(selectedMonth: LocalDate, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 클릭 가능한 월/년도 텍스트
        TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedMonth.format(DateTimeFormatter.ofPattern("yyyy.MM")),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown),
                    contentDescription = "Select Month",
                    tint = Black
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        // 여기에 다른 UI 요소 (예: 필터 아이콘 등)가 올 수 있습니다.
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalysisRecordList(
    groupedResults: List<AnalysisGroup>,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedResults.forEach { group ->
            item {
                // 날짜 헤더 (예: 2025년 9월 14일)
                Text(
                    text = group.date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
                    style = MaterialTheme.run { typography.titleMedium.copy(fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            // 해당 날짜의 분석 결과 카드 목록 (가로 스크롤)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(group.results) { result ->
                        // 재사용 가능한 분석 결과 카드 컴포넌트
                        RiskResultCard(
                            result = result,
                            onClick = { navController.navigate("search_result") }
                        )
                    }
                }
            }
        }
    }
}