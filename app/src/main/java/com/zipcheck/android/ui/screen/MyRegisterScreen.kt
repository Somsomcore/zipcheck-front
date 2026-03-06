package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zipcheck.android.data.model.mypage.MyReportTab
import com.zipcheck.android.data.model.mypage.RegistrationStatus
import com.zipcheck.android.ui.component.common.CustomTopBar
import com.zipcheck.android.ui.component.mypage.MyReportCard
import com.zipcheck.android.ui.state.MyReportsUiState
import com.zipcheck.android.ui.theme.BGGray
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.viewmodel.MyRegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRegisterScreen(
    navController: NavHostController,
    viewModel: MyRegisterViewModel = viewModel(),
    accessToken : String
) {
    var tab by remember { mutableStateOf(MyReportTab.RECEIVED) }
    val uiState by viewModel.uiState.collectAsState()

    // ✅ 탭이 바뀔 때마다 fetch 다시 실행
    LaunchedEffect(tab) {
        val status = when (tab) {
            MyReportTab.RECEIVED -> RegistrationStatus.PENDING
            MyReportTab.REGISTERED -> RegistrationStatus.APPROVED
        }
        viewModel.fetchMyReports(
            token = accessToken,
            status = status,
            page = 0,
            size = 20
        )
    }

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("내가 쓴 신고글", navController, "my_page")
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
                .background(BGGray)
        ) {
            TabRow(
                selectedTabIndex = tab.ordinal,
                containerColor = White,
                contentColor = MainBlue,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[tab.ordinal]),
                        color = MainBlue
                    )
                }
            ) {
                MyReportTab.values().forEachIndexed { index, t ->
                    Tab(
                        selected = tab.ordinal == index,
                        onClick = { tab = t },
                        text = { Text(t.label) },
                        selectedContentColor = MainBlue,
                        unselectedContentColor = Color(0xFF9AA3B2)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // UI 상태에 따른 분기 처리
            when (val state = uiState) {
                MyReportsUiState.Loading -> {
                    // 로딩 중
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MainBlue)
                    }
                }
                is MyReportsUiState.Error -> {
                    // 오류 발생
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "데이터 로딩 중 오류 발생: ${state.message}",
                            color = Color.Red
                        )
                    }
                }
                is MyReportsUiState.Success -> {
                    // 성공적으로 데이터 로드
                    val list = state.items

                    // 개수 문구
                    Text(
                        text = "${list.size}건의 신고가 ${if (tab == MyReportTab.REGISTERED) "등록" else "접수"}되었어요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    // 리스트 표시
                    if (!list.isEmpty()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(list, key = { it.id }) { item ->
                                MyReportCard(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}