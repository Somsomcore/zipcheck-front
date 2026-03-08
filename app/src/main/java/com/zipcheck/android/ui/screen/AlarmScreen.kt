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
import com.zipcheck.android.ui.viewmodel.AlarmViewModel
import androidx.compose.runtime.getValue

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