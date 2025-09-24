package com.zipcheck.android.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.CircleBGGray
import com.zipcheck.android.ui.theme.CircleRed
import com.zipcheck.android.ui.theme.DarkBlack
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.SectionGray

@Composable
fun SearchResultScreen(navController: NavController) {

    val scrollState = rememberScrollState()

    // 화면 전체를 Column으로 구성
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
        ) {
            // 상단 바 (뒤로 가기 버튼과 제목)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // 뒤로 가기 버튼
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(24.dp)
                        .padding(start = 4.dp)
                        .clickable { navController.popBackStack() } // 클릭 시 이전 화면으로 돌아감
                )
                // 화면 제목
                Text(
                    text = "분석 결과",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // ✅ LinearProgressIndicator 추가
            LinearProgressIndicator(
                progress = 3f / 3f, // 세 번째 화면이므로 100% 진행률
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = MainBlue, // 파란색
                trackColor = Gray // 배경색
            )

            Spacer(modifier = Modifier.height(24.dp)) // 두 줄 사이 간격

            // "아주 위험" 텍스트와 설명
            Text(
                text = buildAnnotatedString {
                    append("해당 매물의 위험도는 ")
                    withStyle(
                        style = SpanStyle(
                            color = CircleRed,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("아주 위험")
                    }
                    append(" 입니다")
                },
                fontSize = 16.sp,
                color = Black
            )

            Spacer(modifier = Modifier.height(2.dp)) // 두 줄 사이 간격

            // 두 번째 줄: "동일 면적·거래가 매물 대비 보증금이 10% 높습니다."
            // 일부 텍스트에만 빨간색과 굵은 글씨체 적용
            Text(
                text = buildAnnotatedString {
                    append("동일 면적·거래가 매물 대비 보증금이 ")
                    withStyle(
                        style = SpanStyle(
                            color = CircleRed,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("10%")
                    }
                    append(" 높습니다")
                },
                fontSize = 14.sp,
                color = DarkBlack
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 위험도 섹션
            RiskLevelSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 최우선 변제 금액 섹션
            PriorityRepaymentSection()
        }

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            modifier = Modifier.padding(vertical = 32.dp),
            color = SectionGray,
            thickness = 12.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            // "어떤 기준으로 전세 사기를 진단했나요?" 섹션
            Text(
                text = "Q. 어떤 기준으로 \n\t전세 사기를 진단했나요?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 동면적/동거래 매물 대비 섹션
            ComparisonSection()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "*본 플랫폼은 전세사기 예방을 위한 정보 제공만을 목적으로 하며, 법적 책임을 부담하지 않습니다. \n" +
                        " 계약 체결 전에는 반드시 전문가의 확인을 권장하며, 정보 이용에 따른 모든 책임은 이용자 본인에게 있습니다.",
                fontSize = 8.sp,
                color = PlaceholderGray
            )
        }
    }
}

@Composable
fun RiskLevelSection() {
    // 원형 그래프와 텍스트를 겹쳐서 배치
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // 원형 그래프 (Canvas)
            Canvas(modifier = Modifier.size(150.dp)) {
                val strokeWidth = 25.dp.toPx()
                // 배경 원
                drawCircle(
                    color = CircleBGGray,
                    style = Stroke(width = strokeWidth)
                )
                // 88% 진행 원호
                drawArc(
                    color = CircleRed,
                    startAngle = -90f,
                    sweepAngle = 360f * 0.88f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 중앙의 88% 텍스트
            Text(
                text = "88%",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = CircleRed
            )
        }
    }
}

@Composable
fun PriorityRepaymentSection() {
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
            Text(text = "5,500만원", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "최우선변제액", fontSize = 16.sp, color = PlaceholderGray)
            Text(text = "5,500만원", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComparisonSection() {
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
        description = "주의 동면적, 동거래가 매물 대비 평균은 xxxx원,\n" +
                "중앙값은 xxxx원, 최저가는 xxxx원, 최고가는 xxxx원입니다.\n" +
                "해당 매물은 최저가~최고가 범위 내에 존재합니다."
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