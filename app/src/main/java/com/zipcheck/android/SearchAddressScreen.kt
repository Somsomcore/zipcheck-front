package com.zipcheck.android

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.ExampleTextGray
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.LightBlack
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.OldAddressLabelColor
import com.zipcheck.android.ui.theme.RoadAddressLabelColor
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SearchAddressScreen(navController: NavController) {
    // Correctly declare state variables at the top of the Composable
    var searchText by remember { mutableStateOf("") }
    val allAddresses = remember {
        listOf(
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16"),
            AddressResult("11913", "경기도 구리시 인창2로 65", "경기도 구리시 인창동 640-16")
        )
    }

    val filteredResults = if (searchText.isNotBlank()) {
        allAddresses.filter { address ->
            address.roadAddress.contains(searchText, ignoreCase = true) ||
                    address.oldAddress.contains(searchText, ignoreCase = true)
        }
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        // 상단 바 (뒤로 가기 버튼과 제목)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // 뒤로 가기 버튼
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .padding(start = 4.dp)
                    .clickable { navController.popBackStack() }
            )
        }

        Text(
            "주소를 선택해주세요",
            style = MaterialTheme.typography.titleLarge,
            color = Black,
            fontWeight = FontWeight.Bold
        )

        // Search TextField
        SearchTextField(
            searchText = searchText,
            onValueChange = { newText -> searchText = newText }
        )

        // Add a Spacer for some vertical separation
        Spacer(modifier = Modifier.height(16.dp))

        // Conditional rendering logic
        if (searchText.isEmpty()) {
            AddressInputExample()
        } else {
            AddressResultsList(
                results = filteredResults,
                navController = navController
            )
        }
    }
}

@Composable
fun AddressInputExample() {
    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "입력 예시",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "도로명 + 건물번호",
            color = ExampleTextGray,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "남대문로 9길 40",
            color = MainBlue,
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "지역명(동/리) + 번지",
            color = ExampleTextGray,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "중구 다동 155",
            color = MainBlue,
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "지역명(동/리) + 건물명",
            color = ExampleTextGray,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "분당 주공",
            color = MainBlue,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SearchTextField(
    searchText: String,
    onValueChange: (String) -> Unit
) {
    val textFieldColors = TextFieldDefaults.colors(
        // Set all background and border colors to transparent to remove the default box
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = MainBlue, // This is for the bottom line when focused
        unfocusedIndicatorColor = MainBlue, // For the bottom line when unfocused
        cursorColor = MainBlue,
    )

    TextField(
        value = searchText,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "지번, 도로명, 건물명으로 검색",
                color = Gray
            )
        },
        // The magnifying glass icon on the right
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_address_search), // Replace with your search icon
                contentDescription = "Search",
                tint = MainBlue
            )
        },
        colors = textFieldColors,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search // 스마트폰 키보드 오른쪽 하단의 버튼 모양 변경(검색 버튼)
        ),
    )
}

data class AddressResult(
    val zipCode: String,
    val roadAddress: String,
    val oldAddress: String
)

@Composable
fun AddressResultsList(
    results: List<AddressResult>,
    navController: NavController
) {
    LazyColumn {
        items(results) { address ->
            AddressResultItem(address = address, navController = navController)
        }
    }
}

@Composable
fun AddressResultItem(
    address: AddressResult,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clickable {
//                // 클릭 시 내비게이션 로직 실행
//                // 1. 도로명 주소를 URL 인코딩하여 특수문자 문제 해결
//                val encodedAddress = URLEncoder.encode(address.roadAddress, StandardCharsets.UTF_8.toString())
//
//                // 2. 다음 화면으로 이동하며 인코딩된 주소를 인수로 전달
//                navController.navigate("input_address_detail_screen/$encodedAddress")
                // 1. 이전 화면으로 전달할 결과를 savedStateHandle에 저장
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selectedAddress", address.roadAddress)

                // 2. 이전 화면으로 돌아가기 (popBackStack)
                navController.popBackStack()
            }
    ) {
        // Zip code at the top
        Text(
            text = address.zipCode,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Road address row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "도로명" label
            AddressLabel(
                text = "도로명",
                backgroundColor = RoadAddressLabelColor,
                textColor = MainBlue
            )

            // Road address text
            Text(
                text = address.roadAddress,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Old address row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "구주소" label
            AddressLabel(
                text = "구주소",
                backgroundColor = OldAddressLabelColor,
                textColor = LightBlack
            )

            // Old address text
            Text(
                text = address.oldAddress,
                modifier = Modifier.padding(start = 8.dp),
                color = ExampleTextGray
            )
        }

        Divider(
            modifier = Modifier.padding(top = 12.dp),
            color = OldAddressLabelColor,
            thickness = 1.dp
        )
    }
}

@Composable
fun AddressLabel(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}