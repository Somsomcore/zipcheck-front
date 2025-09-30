package com.zipcheck.android.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZipcheckfrontTheme {
                val navController = rememberNavController()
                Scaffold(
                    containerColor = Color.White,   // 배경 흰색
                    contentColor = Color.Black
                ) { innerPadding ->
                    // NavController로 화면 전환 설정
                    NavHost(
                        navController = navController,
                        startDestination = "fraud_reg_inquiry_detail", //main_screen
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        //LoginScreen route
                        composable("login_screen") {
                            LoginScreen(navController = navController)
                        }
                        //LoginScreen_name route
                        composable("login_screen_name") {
                            NameInputScreen(navController = navController)
                        }
                        composable(
                            route = "login_screen_telecom/{name}",
                            arguments = listOf(navArgument("name") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name")
                            if (name != null) {
                                CarrierInputScreen(navController = navController, name = name)
                            } else {
                                navController.popBackStack()
                            }
                        }
                        composable("fraud_reg_inquiry_detail") {
                            FraudRegInquiryDetailScreen(navController = navController)
                        }
                        composable(
                            route = "fraudRegInquiry?showPopup={showPopup}",
                            arguments = listOf(
                                navArgument("showPopup") { defaultValue = "false" }
                            )
                        ) { backStackEntry ->
                            val showPopup = backStackEntry.arguments?.getString("showPopup") == "true"
                            FraudRegInquiryScreen(navController, showPopup)
                        }

                        // MainScreen route
                        composable("main_screen") {
                            MainScreen(navController = navController)
                        }
                        // Other screen routes
                        composable("search") {
                            SearchScreen(navController = navController)
                        }
                        composable("search_address") {
                            SearchAddressScreen(navController = navController)
                        }
                        composable(
                            "input_address_detail_screen/{roadAddress}",
                            arguments = listOf(navArgument("roadAddress") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val roadAddress = backStackEntry.arguments?.getString("roadAddress")
                            if (roadAddress != null) {
                                // 여기에서 다음 화면 컴포저블을 호출하고, 주소 데이터를 전달
                                InputAddressDetailScreen(navController = navController, roadAddress = roadAddress)
                            }
                        }
                        composable("search_second") {
                            SearchSecondScreen(navController = navController)
                        }
                        composable("search_result") {
                            SearchResultScreen(navController = navController)
                        }
                        composable("map") {
                            MapScreen()
                        }
                        composable("fraud_history") {
                            FraudHistoryScreen()
                        }
                        composable("register") {
                            RegisterScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier           // ✅ Scaffold가 준 innerPadding 받기
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 1) 상단 바 (가운데 제목 + 우측 아이콘)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "ZipCheck",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Icon(
                painter = painterResource(id = R.drawable.profile_user),
                contentDescription = "User Profile",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
                    .size(24.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // 2) 배너 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(2.5.dp, Color.Black),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp)   // ⬅ 필요 높이만큼 늘어나게(고정 250dp 대신 권장)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "안전하게 집을 \n거래할 수 있는 \nLH를 추천",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("<LH Link>", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                }
                Icon(
                    painter = painterResource(id = R.drawable.main_icon),
                    contentDescription = "LH-link",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(104.dp)
                )
            }
        }

        // 3) 카드 아래 구분선(위/아래 간격 넉넉히)
        Divider(
            modifier = Modifier.padding(vertical = 32.dp),
            color = Color.Black,
            thickness = 2.5.dp
        )

        // 4) 버튼 2×2
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ButtonWithIcon("조회", painterResource(id = R.drawable.main_search), Modifier.weight(1f), onClick = { navController.navigate("search") })
                ButtonWithIcon("탐색", painterResource(id = R.drawable.main_map), Modifier.weight(1f), onClick = { navController.navigate("map") })
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ButtonWithIcon("사기 이력", painterResource(id = R.drawable.main_history_fraud), Modifier.weight(1f), onClick = { navController.navigate("fraud_history") })
                ButtonWithIcon("등록", painterResource(id = R.drawable.main_register), Modifier.weight(1f), onClick = { navController.navigate("register") })
            }
        }
    }
}

@Composable
fun ButtonWithIcon(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.5.dp, Color.Black),
        color = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 16.dp)
            )
            Text(text)
        }
    }
}