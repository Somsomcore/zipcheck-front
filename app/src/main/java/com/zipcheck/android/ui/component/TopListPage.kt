package com.zipcheck.android.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.BtNavGray
import com.zipcheck.android.ui.theme.MainBlue
import kotlinx.coroutines.delay

@Composable
private fun ReportCard(
    badge: String,
    title: String,
    sub: String,
    count: String,
    chip1: String,
    chip2: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                // 상단: 타입 배지 + 제목
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TypeBadge(badge)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF2B2F43))
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
                )
                Spacer(Modifier.height(10.dp))
                Row {
                    Text(
                        text = "신고 누적 횟수  ",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
                    )
                    Text(
                        text = count,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2A62F4))
                    )
                }
                Spacer(Modifier.height(30.dp))
                Row {
                    SoftChip(text = chip1, bg = Color(0xFFFFF3C4), fg = Color(0xFF9F7A00))
                    Spacer(Modifier.width(8.dp))
                    SoftChip(text = chip2, bg = Color(0xFFFFF3C4), fg = Color(0xFF9F7A00))
                }
            }
        }
    }
}

@Composable
private fun TypeBadge(text: String) {
    Box(
        modifier = Modifier
            .background(color = MainBlue, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun SoftChip(text: String, bg: Color, fg: Color) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text,
            color = fg,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopReportsCarousel(
    tabs: List<String>,
    autoScrollMillis: Long = 3000L
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    // ── 자동 스와이프
    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(autoScrollMillis)
            val next = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(next)
        }
    }

    // ── 페이지(좌우 스와이프)
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        pageSpacing = 12.dp,
        beyondViewportPageCount = 0
    ) { page ->
        ReportCard(
            badge = tabs[page], // "아파트" 등
            title = "경기도 구리시 인창2로 65 (인창동)",
            sub = "힐스테이트 구리역 105동 1604호",
            count = "n회",
            chip1 = "#" + "깡통전세",
            chip2 = "#" + "깡통전세"
        )
    }

    Spacer(Modifier.height(4.dp))

    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) MainBlue else BorderGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(5.dp)
            )
        }
    }
}