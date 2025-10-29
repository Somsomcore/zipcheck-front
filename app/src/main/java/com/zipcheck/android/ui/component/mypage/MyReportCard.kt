//package com.zipcheck.android.ui.component.mypage
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.zipcheck.android.data.model.mypage.MyReportItem
//import com.zipcheck.android.data.model.mypage.MyReports
//import com.zipcheck.android.ui.theme.BorderGray
//import com.zipcheck.android.ui.theme.MainBlue
//import com.zipcheck.android.ui.theme.White
//
//@Composable
//private fun MyReportCard(item: MyReports) {
//    Card(
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = White),
//        border = BorderStroke(1.dp, BorderGray),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(Modifier.padding(16.dp)) {
//
//            // 1) 상단: 배지 + 주소(큰 제목)
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                TypeBadge(item.typeName)
//                Spacer(Modifier.width(8.dp))
//                Text(
//                    text = item.address,
//                    style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF2B2F43))
//                )
//            }
//
//            // 2) 상세 주소
//            Spacer(Modifier.height(6.dp))
//            Text(
//                text = item.addressDetail,
//                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
//            )
//
//            // 3) 좌측 정보(카테고리/작성자 등) + 우측 상태/날짜
//            Spacer(Modifier.height(10.dp))
//            Row {
//                Column(Modifier.weight(1f)) {
//                    InfoLine(label = "작성자", value = item.reporter)
//                    InfoLine(label = "유형", value = item.contractType)
//                }
//                Column(horizontalAlignment = Alignment.End) {
//                    Text(
//                        text = item.status,
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = MainBlue,
//                            fontWeight = FontWeight.SemiBold
//                        )
//                    )
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        text = item.contractAt,
//                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
//                    )
//                }
//            }
//
//            val showChip1 = !item.chip1.isNullOrBlank()
//
//            if (showChip1) {
//                Spacer(Modifier.height(14.dp))
//                Row {
//                    SoftChip(text = item.chip1!!, bg = SoftBg, fg = SoftFg)
//                    Spacer(Modifier.width(8.dp))
//                }
//            }
//        }
//    }
//}
//
//// === 작은 구성요소들 ===
//@Composable
//private fun TypeBadge(text: String) {
//    Surface(
//        color = MainBlue,
//        shape = RoundedCornerShape(10.dp)
//    ) {
//        Text(
//            text = text,
//            color = Color.White,
//            fontSize = 12.sp,
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//        )
//    }
//}
//
//@Composable
//private fun InfoLine(label: String, value: String) {
//    Row {
//        Text(
//            text = "$label  ",
//            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA3B2))
//        )
//        Text(
//            text = value,
//            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2B2F43))
//        )
//    }
//}
//
//@Composable
//private fun SoftChip(text: String, bg: Color, fg: Color) {
//    Surface(color = bg, shape = RoundedCornerShape(50)) {
//        Text(
//            text = text,
//            color = fg,
//            fontSize = 12.sp,
//            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
//        )
//    }
//}
//
//// 클릭 리플 제거용 간단 확장
//@Composable
//private fun Modifier.noRippleClick(onClick: () -> Unit): Modifier =
//    this.then(Modifier.clickable(
//        indication = null,
//        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
//    ) { onClick() })