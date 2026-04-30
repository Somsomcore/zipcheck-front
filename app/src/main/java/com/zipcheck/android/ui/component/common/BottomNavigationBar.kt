package com.zipcheck.android.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.BackGround
import com.zipcheck.android.ui.theme.BtNavGray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.White
import kotlin.collections.contains

// 하단 탭 항목 정의
sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Home : BottomNavItem("홈", R.drawable.ic_home, "main_screen")
    object Search : BottomNavItem("신고 탐색", R.drawable.ic_location, "map")
    object Risk : BottomNavItem("위험도 조회", R.drawable.ic_list, "search") // 가상의 경로
    object My : BottomNavItem("마이", R.drawable.ic_profile, "my_page") // 가상의 경로
}

// 하단 내비게이션 바 Composable
@Composable
public fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Risk,
        BottomNavItem.My
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = White,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(56.dp),
        border = BorderStroke(1.dp, BackGround)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = when (item) {
                    BottomNavItem.Home  -> currentRoute == BottomNavItem.Home.screen_route
                    BottomNavItem.Search-> currentRoute in listOf("map", "search_address", "input_address_detail_screen/{roadAddress}")
                    BottomNavItem.Risk  -> currentRoute == BottomNavItem.Risk.screen_route
                    BottomNavItem.My    -> currentRoute == BottomNavItem.My.screen_route
                }

                CustomBottomNavigationItem(
                    item = item,
                    selected = selected
                ) {
                    navController.navigate(item.screen_route) {
                        navController.graph.startDestinationRoute?.let { start ->
                            popUpTo(start) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

// 개별 하단 탭 항목 Composable
@Composable
fun CustomBottomNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .size(56.dp)
            .padding(vertical = 4.dp)
            // 클릭 리스너 설정
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            tint = if (selected) MainBlue else BtNavGray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MainBlue else BtNavGray
        )
    }
}