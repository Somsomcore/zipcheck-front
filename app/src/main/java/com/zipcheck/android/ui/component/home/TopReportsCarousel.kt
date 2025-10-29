package com.zipcheck.android.ui.component.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zipcheck.android.data.model.report.ReportItem
import kotlinx.coroutines.delay
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.MainBlue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopReportsCarousel(
    items: List<ReportItem>,
    autoScrollMillis: Long = 3000L,
    onItemSelected: (typeId: Int) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("신고된 주소 없음", color = Color.Gray)
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { items.size })

    // 자동 스와이프 + 선택 이벤트
    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(autoScrollMillis)
            val next = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(next)
            onItemSelected(items[next].typeId)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        pageSpacing = 12.dp,
        beyondViewportPageCount = 0
    ) { page ->
        val item = items[page]
        ReportCard(
            badge = item.typeName,              // ex) "아파트"
            title = item.addr,                  // ex) "경기도 구리시 인창2로 65 (인창동)"
            sub   = item.addrDetail,            // ex) "힐스테이트 구리역 105동 1604호"
            count = item.countText,             // ex) "12회"
            chip1 = item.chip1,                 // ex) "#깡통전세"
            chip2 = item.chip2
        )
    }

    Spacer(Modifier.height(4.dp))

    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { idx ->
            val color = if (pagerState.currentPage == idx) MainBlue else BorderGray
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