package com.zipcheck.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.CircleRed
import com.zipcheck.android.ui.theme.DarkBlack
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.SectionGray
import com.zipcheck.android.ui.theme.White

@Composable
fun RiskAnalysisResultScreen(navController: NavHostController) {

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("분석 결과", navController)
        }
    ) { innerPadding ->
        // 화면 전체를 Column으로 구성
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
            ) {
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
                RiskLevelSection(
                    circleSize = 200.dp,
                    strokeWidth = 25.dp,
                    fontSize = 48,
                    riskColor = CircleRed,
                    riskScore = 88.0
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 최우선 변제 금액 섹션
//                PriorityRepaymentSection()
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
//                ComparisonSection()

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
}