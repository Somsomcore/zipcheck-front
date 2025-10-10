package com.zipcheck.android.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.ExampleTextGray
import com.zipcheck.android.ui.theme.MainBlue
import java.time.LocalDate
import java.time.Month
import kotlin.math.floor

// 구현의 간소화를 위해 기본 Scrollable Column 대신 LazyColumn을 활용하여 휠 효과를 냅니다.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthYearPicker(
    onCancel: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit
) {
    val currentYear = LocalDate.now().year
    val years = (currentYear - 3..currentYear + 2).toList()
    val months = Month.values().toList()

    // 초기 선택 값
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 년/월 휠 선택기
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 년도 선택 휠
            WheelPicker(
                items = years,
                initialIndex = years.indexOf(currentYear),
                onItemSelected = { selectedYear = it },
                modifier = Modifier.weight(1f),
                labelSuffix = "년"
            )

            // 월 선택 휠
            WheelPicker(
                items = months.map { it.value },
                initialIndex = LocalDate.now().monthValue - 1,
                onItemSelected = { selectedMonth = it },
                modifier = Modifier.weight(1f),
                labelSuffix = "월"
            )
        }

        // 버튼 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 1.dp), // 버튼 윗줄에 얇은 구분선처럼 보이도록
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 취소 버튼
            Button(
                onClick = onCancel,
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
                onClick = { onConfirm(selectedYear, selectedMonth) },
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

// 휠 피커 컴포넌트 (LazyColumn을 이용한 단순화된 구현)
@Composable
fun WheelPicker(
    items: List<Int>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    labelSuffix: String = ""
) {
    val visibleItemCount = 5
    val itemHeight = 40.dp
    val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState(initialFirstVisibleItemIndex = initialIndex - floor(visibleItemCount / 2.0).toInt())

    // 스크롤이 멈췄을 때 중앙 아이템 선택
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val centerIndex = lazyListState.firstVisibleItemIndex + floor(visibleItemCount / 2.0).toInt()
            if (centerIndex in items.indices) {
                onItemSelected(items[centerIndex])
            }
        }
    }

    // 초기 로딩 시 중앙 아이템 선택
    LaunchedEffect(Unit) {
        onItemSelected(items[initialIndex])
    }

    // LazyColumn을 사용하여 휠 효과 구현
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items) { item ->
            val isSelected = item == items[lazyListState.firstVisibleItemIndex + floor(visibleItemCount / 2.0).toInt()]

            Text(
                text = "$item$labelSuffix",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                    color = if (isSelected) Black else ExampleTextGray
                ),
                modifier = Modifier
                    .height(itemHeight)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
    }
}