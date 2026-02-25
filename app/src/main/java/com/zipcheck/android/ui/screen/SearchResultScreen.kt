package com.zipcheck.android.ui.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.navercorp.nid.NaverIdLoginSDK.getAccessToken
import com.zipcheck.android.R
import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.model.report.RiskAnlyRequest
import com.zipcheck.android.data.network.RetrofitObj
import com.zipcheck.android.data.repo.RiskRepository
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.component.risk.SearchResultContent
import com.zipcheck.android.ui.state.RiskUiState
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.CircleBGGray
import com.zipcheck.android.ui.theme.CircleRed
import com.zipcheck.android.ui.theme.DarkBlack
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.SectionGray
import com.zipcheck.android.ui.theme.White
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

    val regionCode   = form["regionCode"].orEmpty()
    val deposit      = toIntSafe(form["deposit"])
    val propertyType = form["propertyType"].orEmpty()
    val area         = toIntSafe(form["area"])
    val floor        = toIntSafe(form["floor"])
    val builtYear    = toIntSafe(form["builtYear"])
    val address      = form["address"].orEmpty()
    val addressDetail = form["addressDetail"].orEmpty()

    // 3) 첫 진입 시 분석 호출
    LaunchedEffect(regionCode, encoded) {
        Log.d("SearchResultScreen", "🔹 LaunchedEffect 실행됨 / regionCode=$regionCode")
        if (regionCode.isNotBlank()) {
            Log.d("SearchResultScreen", "🚀 analyze() 호출 시작")
            vm.analyze(
                accessToken = accessToken,
                regionCode = regionCode,
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
                val strokeWidth = strokeWidth.toPx()
                // 배경 원
                drawCircle(
                    color = CircleBGGray,
                    style = Stroke(width = strokeWidth)
                )
                // 88% 진행 원호
                drawArc(
                    color = riskColor,
                    startAngle = -90f,
                    sweepAngle = 360f * 0.88f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 중앙의 88% 텍스트
            Text(
                text = "${riskScore.toInt()}%",
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = CircleRed
            )
        }
    }
}

@Composable
fun PriorityRepaymentSection(
    maxPra: Double,
    pra: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = SectionGray)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "최우선변제금액", fontSize = 16.sp, color = PlaceholderGray)
            Text(text = "${(maxPra / 10000).toInt()}만원", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "최우선변제액", fontSize = 16.sp, color = PlaceholderGray)
            Text(text = "${(pra / 10000).toInt()}만원", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComparisonSection(
    depositPct: Double,
    avg: Double,
    min: Double,
    max: Double,
    stddev: Double
) {
    ComparisonItem(
        icon = painterResource(id = R.drawable.ic_area), // 적절한 아이콘으로 변경
        title = "동면적 매물 대비",
        description = "동면적 매물들의 보증금은 xxx만원, xxx는 xxxx입니다. 현재 매물과 비교해 x% 낮습니다."
    )
    Spacer(modifier = Modifier.height(16.dp))
    ComparisonItem(
        icon = painterResource(id = R.drawable.ic_money), // 적절한 아이콘으로 변경
        title = "동거래가 매물 대비",
        description = "동거래가 매물들의 보증금은 xxx만원, xxx는 xxxx입니다. 현재 매물과 비교해 x% 낮습니다."
    )
    Spacer(modifier = Modifier.height(16.dp))
    ComparisonItem(
        icon = painterResource(id = R.drawable.ic_criteria_result), // 적절한 아이콘으로 변경
        title = "종합 분석 결과",
        description = "주의 동면적, 동거래가 매물 대비 평균은 ${avg}원,\n" +
                "중앙값은 ${stddev}원, 최저가는 ${min}원, 최고가는 ${max}원입니다.\n"
    )
    Spacer(modifier = Modifier.height(16.dp))
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