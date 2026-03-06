package com.zipcheck.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zipcheck.android.ui.component.common.CustomTopBar
import com.zipcheck.android.ui.theme.TopBar
import com.zipcheck.android.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    navController : NavHostController,
    accessToken: String
) {
    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("알림", navController)
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
                .background(White)
        ) {

        }
    }
}