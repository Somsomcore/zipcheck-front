package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zipcheck.android.data.model.alarm.AlarmDTO
import com.zipcheck.android.ui.component.common.CustomTopBar
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.SectionGray
import com.zipcheck.android.ui.theme.ErrorRed
import com.zipcheck.android.ui.theme.DarkBlack
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.ExampleTextGray
import com.zipcheck.android.ui.viewmodel.AlarmViewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    navController : NavHostController,
    accessToken: String,
    viewModel: AlarmViewModel = viewModel()
) {
    // 화면 진입 시 알람 목록 로드 및 확인 처리
    LaunchedEffect(Unit) {
        viewModel.fetchAlarms(accessToken)
        viewModel.confirmAllAlarms(accessToken)
    }

    val alarms by viewModel.alarmList.collectAsState()

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("알림", navController)
        }
    ) { innerPadding ->
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(TopBar)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
        ) {
            if (alarms.isEmpty()) {
                // 알람이 없을 때 표시할 UI
                EmptyAlarmView()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(alarms) { alarm ->
                        AlarmItemView(alarm)
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmItemView(alarm: AlarmDTO) {
    val isRejected = alarm.notificationType.contains("반려") || alarm.notificationContent.contains("반려") || alarm.notificationType == "REJECT"

    val badgeText = if (isRejected) "신고글이 반려되었습니다" else "신고글이 등록되었습니다"
    val badgeBgColor = if (isRejected) Color(0xFFFFEAEB) else SectionGray
    val badgeTextColor = if (isRejected) ErrorRed else DarkBlack

    // Heuristic parsing: Details often contain "|", Bottom info often starts with "ⓘ" or similar
    val formattedDate = alarm.createdAt.substringBefore("T")
    val rawLines = alarm.notificationContent.split("\n")
    val detailLines = rawLines.filter { it.contains("|") }
    // As we can't be sure about the Info character in API, we'll assume the last line might be it if it starts with an info indicator, or just let users see it in Title if it doesn't match
    val bottomLines = rawLines.filter { it.startsWith("ⓘ") || it.startsWith("!") }
    val titleLines = rawLines.filterNot { detailLines.contains(it) || bottomLines.contains(it) }

    val title = titleLines.joinToString("\n").trim()
    val details = detailLines.joinToString("\n").trim()
    val bottomText = bottomLines.joinToString("\n").trim()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF1F2F4), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Left Status Icon
            Image(
                painter = painterResource(
                    id = if (isRejected) com.zipcheck.android.R.drawable.ic_alarm_cancel else com.zipcheck.android.R.drawable.ic_alarm_check
                ),
                contentDescription = "Status Icon",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header: Badge + Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(badgeBgColor, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 13.sp,
                        color = Black
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Text
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                        lineHeight = 22.sp
                    )
                }

                if (details.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = details,
                        fontSize = 13.sp,
                        color = ExampleTextGray,
                        lineHeight = 20.sp
                    )
                }

                if (bottomText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = bottomText,
                        fontSize = 13.sp,
                        color = ExampleTextGray
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyAlarmView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "새로운 알림이 없습니다.", color = Color.Gray)
    }
}