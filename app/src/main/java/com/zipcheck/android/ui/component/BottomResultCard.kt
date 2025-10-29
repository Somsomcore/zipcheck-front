//package com.zipcheck.android.ui.component
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Divider
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.zipcheck.android.ui.theme.Black
//import com.zipcheck.android.ui.theme.White
//
//// MapScreen 컴포저블 외부에 정의
//
//// 이미지 2: 핀 클릭 시 나타나는 상세 정보 카드
//@Composable
//fun BottomResultCard(data: MapPinData, modifier: Modifier = Modifier) {
//    Column(
//        modifier = modifier
//            .background(White, RoundedCornerShape(12.dp))
//            .shadow(4.dp, RoundedCornerShape(12.dp))
//            .padding(16.dp)
//    ) {
//        // 첫 번째 줄: 태그 및 주소
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = data.type,
//                color = White,
//                fontSize = 12.sp,
//                modifier = Modifier
//                    .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp)) // 예시 블루 색상
//                    .padding(horizontal = 6.dp, vertical = 2.dp)
//            )
//            Spacer(Modifier.size(8.dp))
//            Column {
//                Text(data.address, fontWeight = FontWeight.Bold, color = Black, fontSize = 16.sp)
//                Text(data.buildingName, color = Color.Gray, fontSize = 14.sp)
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        // 계약 정보
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text("계약 형태", color = Color.Gray, fontSize = 14.sp)
//            Text(data.deposit, color = Black, fontSize = 14.sp)
//        }
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text("계약 일자", color = Color.Gray, fontSize = 14.sp)
//            Text(data.contractDate, color = Black, fontSize = 14.sp)
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        // 상세 내용 요약 텍스트
//        Text(
//            "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용...",
//            fontSize = 12.sp,
//            color = Color.DarkGray,
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
//                .padding(10.dp)
//        )
//    }
//}
//
//// 이미지 3: 검색 결과 리스트
//@Composable
//fun BottomSearchResultList(
//    results: List<MapPinData>,
//    onItemClick: (MapPinData) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxHeight(0.5f) // 최대 높이를 화면의 절반으로 제한
//            .background(White.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
//            .shadow(4.dp, RoundedCornerShape(12.dp))
//    ) {
//        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
//            items(results) { pin ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { onItemClick(pin) }
//                        .padding(horizontal = 16.dp, vertical = 8.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column {
//                        // 첫 번째 줄: 태그 및 주소/건물명
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Text(
//                                text = pin.type,
//                                color = White,
//                                fontSize = 10.sp,
//                                modifier = Modifier
//                                    .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp))
//                                    .padding(horizontal = 4.dp, vertical = 1.dp)
//                            )
//                            Spacer(Modifier.width(8.dp))
//                            Text(pin.address, fontWeight = FontWeight.Medium, color = Black, fontSize = 14.sp)
//                        }
//                        Text(pin.buildingName, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 50.dp))
//                        Spacer(Modifier.height(4.dp))
//                        // 계약 일자
//                        Text("계약 일자: ${pin.contractDate}", color = Color.Gray, fontSize = 12.sp)
//                    }
//
//                    // 신고 건수 뱃지 (선택 사항: reportCount가 0보다 클 경우)
//                    if (pin.reportCount > 0) {
//                        Text(
//                            text = pin.reportCount.toString(),
//                            color = White,
//                            fontSize = 12.sp,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier
//                                .size(24.dp)
//                                .background(Color.Red, shape = androidx.compose.foundation.shape.CircleShape)
//                                .padding(4.dp)
//                        )
//                    }
//                }
//                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
//            }
//        }
//    }
//}