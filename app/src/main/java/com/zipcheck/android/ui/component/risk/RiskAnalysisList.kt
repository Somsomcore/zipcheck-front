package com.zipcheck.android.ui.component.risk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zipcheck.android.R
import com.zipcheck.android.data.model.report.MyRiskItem
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.ExampleTextGray
import com.zipcheck.android.ui.theme.White

private const val MAX_ITEMS = 5

@Composable
fun RiskAnalysisList(
    results: List<MyRiskItem>,
    onAddClicked: () -> Unit,
    onItemClicked: (MyRiskItem) -> Unit,
    navController: NavHostController
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. 기존 분석 결과 카드 표시 (최대 5개)
            items(results.take(MAX_ITEMS)) { result ->
                RiskResultCard(
                    result = result,
                    onClick = { onItemClicked(result) }
                )
            }

            item {
                AddCard(onClick = onAddClicked)
            }
        }
    }
}

@Composable
fun AddCard(onClick: () -> Unit) {
    Card(
        onClick = { onClick() }, // 등록 화면 연결
        modifier = Modifier
            .width(200.dp) // 결과 카드와 동일한 너비 유지
            .height(250.dp), // 결과 카드와 동일한 높이 유지
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "위험도 분석하러 가기",
                style = MaterialTheme.typography.bodyMedium,
                color = ExampleTextGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "분석 추가",
                tint = Color.Unspecified
            )
        }
    }
}