package com.zipcheck.android.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.zipcheck.android.R
import com.zipcheck.android.data.model.report.MyRiskItem
import com.zipcheck.android.data.model.riskAnalysis.AnalysisGroup
import com.zipcheck.android.data.model.riskAnalysis.RiskAnalysisResult
//import com.zipcheck.android.data.model.riskAnalysis.groupedResults
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.component.MonthYearPicker
import com.zipcheck.android.ui.component.RiskResultCard
//import com.zipcheck.android.ui.component.RiskResultCard
import com.zipcheck.android.ui.theme.BGGray
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.ExampleTextGray
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.viewmodel.MyRiskListViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskAnalysisRecordScreen(
    navController: NavHostController,
    vm: MyRiskListViewModel,
) {
    // 월 선택 바텀 시트 상태 관리
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf(LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue, 1)) }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val results by vm.items.collectAsState()

    LaunchedEffect(Unit) {
        vm.clear()              // items / error / loading 상태 초기화
        selectedMonth = LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue, 1) // 기본 월(표시용)도 초기화
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
                .background(BGGray)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TopBar)
            )

            MonthToggleHeader(
                selectedMonth = selectedMonth,
                onClick = { showBottomSheet = true }
            )

            when {
                loading -> {
                    // 로딩 UI
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("불러오는 중…")
                    }
                }
                error != null -> {
                    // 에러 UI
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("오류: $error", color = Color.Red)
                    }
                }
                results.isEmpty() -> {
                    // 초기/빈 상태 UI
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "월을 선택해 기록을 불러오세요.", style = MaterialTheme.typography.bodyMedium, color = ExampleTextGray)
                    }
                }
                else -> {
                    // 날짜별 그룹핑 후 렌더
                    val groups = groupByDate(results)
                    AnalysisRecordList(
                        groupedResults = groups,
                        navController = navController
                    )
                }
            }
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

                    vm.load(page = 0, size = 10, year = year, month = month)

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
                        val gson = Gson()

                        val json = gson.toJson(result)
                        val encoded = URLEncoder.encode(json, StandardCharsets.UTF_8.name())

                        RiskResultCard(
                            result = result,
                            onClick = {
                                navController.navigate("risk_analysis_result?resultJson=$encoded")
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun MyRiskItem.createdLocalDate(): LocalDate? {
    val s = this.createdAt ?: return null
    // 1) yyyy-MM-dd
    runCatching { return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE) }
    // 2) yyyy-MM-ddTHH:mm:ss[.SSS...]
    runCatching { return java.time.LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate() }
    // 3) offset/zoned (서버가 타임존을 줄 수도 있음)
    runCatching { return java.time.OffsetDateTime.parse(s).toLocalDate() }
    runCatching { return java.time.ZonedDateTime.parse(s).toLocalDate() }
    // 4) 최후: 앞 10자리만 자르기
    return runCatching { LocalDate.parse(s.take(10), DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
}

@RequiresApi(Build.VERSION_CODES.O)
fun groupByDate(results: List<MyRiskItem>): List<AnalysisGroup> {
    val pairs = results.mapNotNull { item ->
        item.createdLocalDate()?.let { it to item }
    }
    return pairs
        .groupBy({ it.first }, { it.second })
        .map { (date, items) -> AnalysisGroup(date = date, results = items) }
        .sortedByDescending { it.date }
}
