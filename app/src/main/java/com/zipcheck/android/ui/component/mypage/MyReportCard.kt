package com.zipcheck.android.ui.component.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zipcheck.android.data.model.mypage.MyReportItem
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.White

@Composable
fun MyReportCard(item: MyReportItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            TypeBadge(item.classificationName)

            Spacer(Modifier.height(16.dp))
            Text(
                text = item.addr,
                style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF2B2F43))
            )

            // 2) 상세 주소
            Spacer(Modifier.height(6.dp))
            Text(
                text = item.addrDetail,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
            )

            Spacer(Modifier.height(16.dp))
            Row {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "주택 종류",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.contractType,
                        style = MaterialTheme.typography.bodySmall.copy(color = Black)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Row {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "계약 형태",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.contractType,
                        style = MaterialTheme.typography.bodySmall.copy(color = Black)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Row {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "계약 일자",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.contractAt,
                        style = MaterialTheme.typography.bodySmall.copy(color = Black)
                    )
                }
            }
        }
    }
}

// === 작은 구성요소들 ===
@Composable
private fun TypeBadge(text: String) {
    Box(
        modifier = Modifier
            .background(color = MainBlue, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) { Text(text.trim(), color = Color.White, fontSize = 12.sp) }
}
