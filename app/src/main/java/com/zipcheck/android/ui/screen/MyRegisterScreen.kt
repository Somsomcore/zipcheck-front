package com.zipcheck.android.ui.screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zipcheck.android.data.model.mypage.MyReportItem
import com.zipcheck.android.data.model.mypage.MyReportTab
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRegisterScreen(
    navController: NavHostController,
    receivedItems: List<MyReportItem>,
    registeredItems: List<Nothing?>,
    defaultTab: MyReportTab = MyReportTab.RECEIVED
) {

    var tab by remember { mutableStateOf(defaultTab) }

    val list = when (tab) {
        MyReportTab.RECEIVED -> receivedItems
        MyReportTab.REGISTERED -> registeredItems
    }

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("내가 쓴 신고글", navController, "my_page")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = tab.ordinal,
                containerColor = White,
                contentColor = MainBlue,
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

            // 개수 문구
            Text(
                text = "${list?.size}건의 신고가 ${if (tab == MyReportTab.REGISTERED) "등록" else "접수"}되었어요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7C8594),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // 리스트
            if (list?.isEmpty() == true) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "표시할 신고가 없습니다.",
                        color = Color(0xFF9AA3B2)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
//                    items(list, key = { it.id }) { item ->
//                        MyReportCard(item = item)
//                    }
                }
            }
        }
    }
}