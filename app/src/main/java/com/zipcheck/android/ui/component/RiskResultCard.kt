package com.zipcheck.android.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zipcheck.android.data.model.riskAnalysis.RiskAnalysisResult
import com.zipcheck.android.ui.screen.RiskLevelSection
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.CircleGreen
import com.zipcheck.android.ui.theme.CircleOrange
import com.zipcheck.android.ui.theme.CircleRed

@Composable
fun RiskResultCard(
    result: RiskAnalysisResult,
    onClick: () -> Unit
) {
    val riskColor = when (result.riskLevel) {
        "Critical" -> CircleRed // 빨간색 (88%)
        "Danger" -> CircleOrange // 주황색 (60%)
        else -> CircleGreen // 기본/안전 (녹색)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp) // 적절한 너비 지정
            .height(250.dp), // 적절한 높이 지정
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            // 주소
            Text(result.address, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            Text(result.addressDetail, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(48.dp))

            // 원형 그래프
            RiskLevelSection(
                circleSize = 100.dp,
                strokeWidth = 14.dp,
                fontSize = 24,
                riskColor = riskColor,
                riskScore = result.riskScore
            )
        }
    }
}