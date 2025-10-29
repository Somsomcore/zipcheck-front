package com.zipcheck.android.ui.component.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.model.report.toReportItem
import com.zipcheck.android.ui.state.TopReportsUiState
import com.zipcheck.android.ui.theme.ExampleTextGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TopReportsSection(
    reportService: ReportService,
    accessToken: String,
    addBearerIfMissing: Boolean = true
) {
    val finalToken = remember(accessToken, addBearerIfMissing) {
        if (addBearerIfMissing && !accessToken.startsWith("Bearer ")) "Bearer $accessToken" else accessToken
    }

    // API 로딩
    val uiState by produceState<TopReportsUiState>(initialValue = TopReportsUiState.Loading, finalToken) {
        value = TopReportsUiState.Loading
        try {
            val response = withContext(Dispatchers.IO) {
                reportService.getTop5Report(finalToken).execute()
            }

            Log.d("TopReportsAPI", "✅ Response code: ${response.code()}")
            Log.d("TopReportsAPI", "✅ isSuccessful: ${response.isSuccessful}")
            Log.d("TopReportsAPI", "✅ message: ${response.message()}")
            Log.d("TopReportsAPI", "✅ rawBody: ${response.raw()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("TopReportsAPI", "✅ body: $body")
                val items = body?.result?.top5Reports.orEmpty().map { it.toReportItem() }
                Log.d("TopReportsAPI", "✅ mapped items size: ${items.size}")
                value = TopReportsUiState.Success(items)
            } else {
                Log.e("TopReportsAPI", "❌ HTTP ${response.code()} - ${response.message()}")
                Log.e("TopReportsAPI", "❌ errorBody: ${response.errorBody()?.string()}")
                value = TopReportsUiState.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("TopReportsAPI", "❌ Exception occurred", e)
            value = TopReportsUiState.Error(e.message ?: "네트워크 오류")
        }
    }

    // 선택된 타입 id 전달용 상태
    var selectedTypeId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "피해 신고 집중 접수 주소지 TOP 5",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is TopReportsUiState.Loading -> {
                Text("데이터를 불러오는 중...", color = ExampleTextGray)
                Spacer(Modifier.height(16.dp))
            }

            is TopReportsUiState.Error -> {
                Text("TOP 5 정보를 불러오지 못했습니다.", color = Color.Red)
                Spacer(Modifier.height(16.dp))
            }

            is TopReportsUiState.Success -> {
                val items = state.items

                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "현재 접수된 신고 데이터가 없습니다.",
                            color = ExampleTextGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LaunchedEffect(items) {
                        if (selectedTypeId == null) {
                            selectedTypeId = items.first().typeId
                        }
                    }

                    TopReportsCarousel(
                        items = items,
                        autoScrollMillis = 3500L,
                        onItemSelected = { typeId ->
                            selectedTypeId = typeId
                            println("Carousel에서 선택된 주택 유형 ID: $typeId")
                        }
                    )
                }
            }
        }
    }
}