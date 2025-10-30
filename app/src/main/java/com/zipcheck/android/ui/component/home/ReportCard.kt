package com.zipcheck.android.ui.component.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.MainBlue

@Composable
fun ReportCard(
    badge: String,
    title: String,
    sub: String,
    count: String,
    chip1: String?,
    chip2: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                TypeBadge(badge)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF2B2F43))
                )

                Spacer(Modifier.height(4.dp))
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
                Spacer(Modifier.height(20.dp))

                Row {
                    if (!chip1.isNullOrBlank() && chip1 != "#분류없음") {
                        SoftChip(
                            text = chip1,
                            bg = Color(0xFFFFF3C4),
                            fg = Color(0xFF9F7A00)
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    // chip2는 있을 때만 표시
                    if (!chip2.isNullOrBlank() && chip2 != "#분류없음") {
                        Spacer(Modifier.width(8.dp))
                        SoftChip(
                            text = chip2,
                            bg = Color(0xFFFFF3C4),
                            fg = Color(0xFF9F7A00)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeBadge(text: String) {
    Box(
        modifier = Modifier
            .background(color = MainBlue, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) { Text(text, color = Color.White, fontSize = 12.sp) }
}

@Composable
private fun SoftChip(text: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(text, color = fg, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}