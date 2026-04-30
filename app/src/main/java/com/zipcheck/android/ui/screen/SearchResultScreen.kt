package com.zipcheck.android.ui.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zipcheck.android.R
import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.model.report.RiskAnlyRequest
import com.zipcheck.android.data.network.RetrofitObj
import com.zipcheck.android.data.repo.RiskRepository
import com.zipcheck.android.ui.component.common.CustomTopBar
import com.zipcheck.android.ui.component.risk.SearchResultContent
import com.zipcheck.android.ui.state.RiskUiState
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.CircleBGGray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.SectionGray
import com.zipcheck.android.ui.theme.TextGreen
import com.zipcheck.android.ui.theme.TextOrange
import com.zipcheck.android.ui.theme.TextRed
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme
import com.zipcheck.android.ui.viewmodel.RiskViewModel

@Composable
fun SearchResultScreen(navController: NavHostController, accessToken: String) {
    val context = LocalContext.current

    val riskService = remember {
        RetrofitObj.getRetrofit(context).create(ReportService::class.java)
    }
    val riskRepo = remember { RiskRepository(riskService) }
    val vm: RiskViewModel = viewModel(
        factory = object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RiskViewModel(riskRepo) as T
            }
        }
    )
    val ui by vm.ui.collectAsState()

    // 2) form 파싱
    val encoded = navController.currentBackStackEntry?.arguments?.getString("form").orEmpty()
    val json = Uri.decode(encoded)
    val mapType = object : TypeToken<Map<String, String>>() {}.type
    val form: Map<String, String> = runCatching {
        Gson().fromJson<Map<String, String>>(json, mapType) ?: emptyMap()
    }.getOrElse { emptyMap() }

    // 안전 변환 헬퍼
    fun toIntSafe(s: String?) = s?.toIntOrNull() ?: 0
    fun toDoubleSafe(s: String?): Double = s?.toDoubleOrNull()?.toDouble() ?: 0.0

    val regionCode   = form["regionCode"].orEmpty()
    val deposit      = toIntSafe(form["deposit"])
    val propertyType = form["propertyType"].orEmpty()
    val area         = toDoubleSafe(form["area"])
    val floor        = toIntSafe(form["floor"])
    val builtYear    = toIntSafe(form["builtYear"])
    val address      = form["address"].orEmpty()
    val addressDetail = form["addressDetail"].orEmpty()

    // 3) 첫 진입 시 분석 호출
    LaunchedEffect(regionCode, encoded) {
        Log.d("SearchResultScreen", "🔹 LaunchedEffect 실행됨 / regionCode=$regionCode")
        val truncatedRegionCode = if (regionCode.length >= 5) {
            regionCode.take(5)
        } else {
            regionCode
        }

        if (truncatedRegionCode.isNotBlank()) {
            Log.d("SearchResultScreen", "🚀 analyze() 호출 시작")
            vm.analyze(
                accessToken = accessToken,
                regionCode = truncatedRegionCode,
                req = RiskAnlyRequest(
                    deposit = deposit,
                    propertyType = propertyType,
                    area = area,
                    floor = floor,
                    buildYear = builtYear,
                    address = address,
                    addressDetail = addressDetail
                )
            )
        } else {
            Log.e("SearchResultScreen", "❌ regionCode가 비어 있어서 analyze() 미실행됨")
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("분석 결과", navController, "main_screen")
        }
    ) { innerPadding ->
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(TopBar)
        )

        when (val s = ui) {
            RiskUiState.Idle, RiskUiState.Loading -> {
                // 로딩 화면
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(color = MainBlue)
                    Spacer(Modifier.height(12.dp))
                    Text("분석 중…", color = Black)
                }
            }

            is RiskUiState.Error -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("오류: ${s.message}", color = Color.Red)
                }
            }

            is RiskUiState.Success -> {
                val data = s.data

                SearchResultContent(
                    innerPadding,
                    scrollState,
                    navController = navController,
                    riskLevel = data.riskLevel,        // e.g., "Critical"
                    riskScore = data.riskScore,        // e.g., 88.0
                    depositPct = data.depositPct,
                    avg = data.average,
                    min = data.minimum,
                    max = data.maximum,
                    maxPra = data.maxPra,
                    pra = data.pra,
                    address = data.address,
                    addressDetail = data.addressDetail,
                    stddev = data.standardDeviation
                )
            }
        }
    }
}

@Composable
fun RiskLevelSection(
    circleSize: Dp,
    strokeWidth: Dp,
    fontSize: Int,
    riskColor: Color,
    txtColor: Color,
    riskScore: Double
) {
    // 원형 그래프와 텍스트를 겹쳐서 배치
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(circleSize),
            contentAlignment = Alignment.Center
        ) {
            // 원형 그래프 (Canvas)
            Canvas(modifier = Modifier.size(150.dp)) {
                val strokeWidth = (strokeWidth * 1.2f).toPx()

                drawCircle(
                    color = CircleBGGray,
                    style = Stroke(width = strokeWidth)
                )

                drawArc(
                    color = riskColor,
                    startAngle = -90f,
                    sweepAngle = 360f * (riskScore.toFloat() / 100),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 중앙의 88% 텍스트
            Text(
                text = "${riskScore.toInt()}%",
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = txtColor
            )
        }
    }
}

@Composable
fun PriorityRepaymentSection(
    maxPra: Double,
    pra: Double
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5), // 연회색 배경
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ComparisonRow("최우선변제금액", "${maxPra.toInt()}만원")
            ComparisonRow("최우선변제액", "${pra.toInt()}만원")
        }
    }
}

@Composable
fun ComparisonSection(
    riskLevel: String,
    depositPct: Double,
    avg: Double,
    min: Double,
    max: Double,
    stddev: Double
) {
    val analysisTexts = getAnalysisTexts(riskLevel, min, max, stddev)

    Column {
        // 1. 유사 매물 보증금 리스트 섹션
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_area), // 돋보기 아이콘
                contentDescription = null,
                tint = MainBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("유사 매물 보증금", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 회색 박스 영역
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF5F5F5), // 연회색 배경
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ComparisonRow("평균", "${avg.toInt()}만원")
                ComparisonRow("최저가", "${min.toInt()}만원")
                ComparisonRow("최고가", "${max.toInt()}만원")
                ComparisonRow("표준편차", "${stddev}")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. 종합 분석 결과 섹션
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_criteria_result), // 리포트 아이콘
                contentDescription = null,
                tint = MainBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("종합 분석 결과", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 분석 결과 텍스트 카드
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF5F5F5),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                analysisTexts.forEachIndexed { index, item ->
                    Column {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                    if (index < analysisTexts.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.DarkGray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ComparisonItem(icon: Painter, title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        // 아이콘과 타이틀을 담을 Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                tint = MainBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 타이틀 사이 간격
            Text(text = title, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 하단: 설명 박스 (라운드 + 연회색 배경)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = SectionGray
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = description,
                modifier = Modifier.padding(14.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

data class RiskAnalysisText(
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun getAnalysisTexts(riskLevel: String, min: Double, max: Double, stddev: Double): List<RiskAnalysisText> {
    return when (riskLevel.lowercase()) {
        "Critical" -> listOf(
            RiskAnalysisText("주변 매물 간 가격 차이가 ${stddev.toInt()}원으로 큽니다.", "시세 조작이나 가짜 매물일 가능성이 높으니 전문가 확인 전에는 가계약을 자제하세요.", TextRed),
            RiskAnalysisText("주변 최고가(${max.toInt()}원)를 초과한 이례적인 가격입니다.", "보증금 미반환 사고 발생 확률이 매우 높은 가격대이므로 계약에 극도로 주의가 필요합니다.", TextRed)
        )
        "Caution" -> listOf(
            RiskAnalysisText("주변 시세가 완만하게 형성되어 있습니다.", "급격한 시세 변동 여부나 최근 해당 법정동의 실거래가 추이를 추가로 확인하세요.", TextOrange),
            RiskAnalysisText("시세 범위 내에 있으나 주의가 필요합니다.", "입지나 시설 조건 대비 보증금이 과하게 책정된 것은 아닌지 신중히 비교하세요.", TextOrange)
        )
        else -> listOf( // 보통/안전
            RiskAnalysisText("매물 간 가격 차이가 적어 시세가 안정적입니다.", "정상적인 시장 가격으로 판단되나, 실제 매물 상태를 현장에서 한 번 더 확인하세요.", TextGreen),
            RiskAnalysisText("주변 시세 범위(${min.toInt()}원~${max.toInt()}원) 내 적정 수준입니다.", "보증금이 적정하나, 근저당 설정 여부 등 등기부상 권리관계를 함께 확인하세요.", TextGreen)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultScreenPreview() {
    ZipcheckfrontTheme {
        val navController = rememberNavController()
        SearchResultScreen(navController, accessToken = "")
    }
}