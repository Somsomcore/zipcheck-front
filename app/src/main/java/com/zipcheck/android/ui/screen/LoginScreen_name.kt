package com.zipcheck.android.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.TextFieldBorderGray

@Composable
fun NameInputScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 뒤로가기 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.Start)
                .size(24.dp)
                .padding(start = 4.dp)
                .clickable {
                    navController.popBackStack()
                }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 제목
        Text(
            text = "이름을 입력해주세요",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 라벨 + 텍스트필드
        Text(
            text = "이름",
            style = MaterialTheme.typography.titleMedium
        )

        var name by remember { mutableStateOf("") }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            singleLine = true,
            colors = androidx . compose . material3 . OutlinedTextFieldDefaults.colors(
                focusedTextColor = Black, // 입력 중 텍스트 색상
                unfocusedTextColor = Black, // 입력 안 할 때 텍스트 색상
                focusedBorderColor = MainBlue, // 포커스 됐을 때 테두리 색상
                unfocusedBorderColor = TextFieldBorderGray // 포커스 안 됐을 때 테두리 색상
        )
        )
        Spacer(modifier = Modifier.height(30.dp))

        // 예시 버튼 (실제 앱의 디자인에 맞춰 수정하세요)
        Button(
            onClick = {
                navController.navigate("login_screen_telecom/$name")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() // 이름이 비어있지 않을 때만 활성화
        ) {
            Text("다음")
        }
    }
}
