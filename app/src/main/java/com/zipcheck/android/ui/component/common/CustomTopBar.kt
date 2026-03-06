package com.zipcheck.android.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.White


@Composable
fun CustomTopBar(
    title: String = "",
    navController: NavHostController,
    navigateTo: String? = null, // 🔹 추가
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 16.dp)
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        // 뒤로 가기 버튼
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(32.dp)
                .padding(start = 4.dp)
                .clickable (
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ){
                    if (navigateTo != null) {
                        navController.navigate(navigateTo) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
        )
        // 화면 제목
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Black
        )
    }
}
