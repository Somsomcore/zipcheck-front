package com.zipcheck.android.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.BackGround
import com.zipcheck.android.ui.theme.BtNavGray
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures!!) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val keyHash = String(Base64.encode(md.digest(), Base64.NO_WRAP))
//                Log.d("KeyHash", "키 해시: $keyHash")
//            }
//        } catch (e: Exception) {
//            Log.e("KeyHash", "키 해시 얻기 실패", e)
//        }

        setContent {
            ZipcheckfrontTheme {
                val navController = rememberNavController()
                val showBottomBar = rememberSaveable { mutableStateOf(true) }

                Scaffold(
                    containerColor = White,   // 배경 흰색
                    contentColor = Color.Black,
                    bottomBar = {
                        if (showBottomBar.value) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    // NavController로 화면 전환 설정
                    NavHost(
                        navController = navController,
                        startDestination = "main_screen",
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        // MainScreen route
                        composable("main_screen") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = true
                            }
                            MainScreen(navController = navController)
                        }
                        // Other screen routes
                        composable("search") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            SearchScreen(navController = navController)
                        }
                        composable("search_address") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
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
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            SearchSecondScreen(navController = navController)
                        }
                        composable("search_result") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            SearchResultScreen(navController = navController)
                        }
                        composable("map") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            MapScreen(navController = navController)
                        }
                        composable("fraud_history") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = true
                            }
                            FraudHistoryScreen()
                        }
                        composable("register") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = true
                            }
                            RegisterScreen()
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun MainScreen(
//    navController: NavHostController,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier           // ✅ Scaffold가 준 innerPadding 받기
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//    ) {
//        Spacer(Modifier.height(8.dp))
//
//        // 2) 배너 카드
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 32.dp, bottom = 16.dp),
//            shape = RoundedCornerShape(14.dp),
//            border = BorderStroke(2.5.dp, Color.Black),
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            elevation = CardDefaults.cardElevation(0.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(min = 250.dp)   // ⬅ 필요 높이만큼 늘어나게(고정 250dp 대신 권장)
//                    .padding(20.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(Modifier.weight(1f)) {
//                    Text(
//                        "안전하게 집을 \n거래할 수 있는 \nLH를 추천",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = Color.Black
//                    )
//                    Spacer(Modifier.height(6.dp))
//                    Text("<LH Link>", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
//                }
//                Icon(
//                    painter = painterResource(id = R.drawable.main_icon),
//                    contentDescription = "LH-link",
//                    tint = Color.Unspecified,
//                    modifier = Modifier.size(104.dp)
//                )
//            }
//        }
//
//        // 3) 카드 아래 구분선(위/아래 간격 넉넉히)
//        Divider(
//            modifier = Modifier.padding(vertical = 32.dp),
//            color = Color.Black,
//            thickness = 2.5.dp
//        )
//
//        // 4) 버튼 2×2
//        Column(
//            Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                ButtonWithIcon("조회", painterResource(id = R.drawable.main_search), Modifier.weight(1f), onClick = { navController.navigate("search") })
//                ButtonWithIcon("탐색", painterResource(id = R.drawable.main_map), Modifier.weight(1f), onClick = { navController.navigate("map") })
//            }
//            Spacer(Modifier.height(16.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                ButtonWithIcon("사기 이력", painterResource(id = R.drawable.main_history_fraud), Modifier.weight(1f), onClick = { navController.navigate("fraud_history") })
//                ButtonWithIcon("등록", painterResource(id = R.drawable.main_register), Modifier.weight(1f), onClick = { navController.navigate("register") })
//            }
//        }
//    }
//}

// MainActivity.kt

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier
            .fillMaxSize()
        // ✅ 수직 스크롤 가능하게 만들 경우: .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()                 // 상태바 피해서
                .padding(horizontal = 16.dp)
        ) {
            // 배너 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3B568B)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                // Row 대신 Box로 배치 자유도 확보
                Box(modifier = Modifier.fillMaxSize()) {

                    // 좌측 텍스트
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 20.dp, top = 60.dp, end = 20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "내 보증금, 과연 안전할까?",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "전세 위험 확인하러 가기 ", // > 문자 제거
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            // ✅ 오른쪽 화살표 아이콘 (적절한 리소스 ID로 변경해주세요. 예: R.drawable.ic_arrow_right)
                            Icon(
                                painter = painterResource(id = R.drawable.ic_next), // 아이콘 리소스 ID
                                contentDescription = "Go",
                                tint = Color.White, // 흰색으로 틴트 적용
                                modifier = Modifier.size(10.dp) // 텍스트 크기에 맞게 크기 조정
                            )
                        }
                    }

                    // ✅ 집 이미지는 Image로, 하단-오른쪽에 적당한 크기로
                    Image(
                        painter = painterResource(id = R.drawable.img_home),
                        contentDescription = "House",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 24.dp, bottom = 36.dp),
                        contentScale = ContentScale.Fit
                    )

                    // 🔔 종 아이콘 — 카드 우상단 ‘안쪽’에 겹치게
                    val interaction = remember { MutableInteractionSource() }
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)               // 카드 우상단
                            .padding(top = 12.dp, end = 12.dp)     // 모서리에서 살짝 안쪽으로
                            .size(40.dp)                           // 배경 캡슐 크기
                            .clickable(                            // 클릭 리플/배경 제거(원하면)
                                interactionSource = interaction,
                                indication = null
                            ) { /* TODO: 알림 화면 이동 */ },
                        shape = RoundedCornerShape(10.dp),         // 둥근 사각
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4F6597)     // 배너색보다 살짝 밝은 파랑 (배경)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) // 미묘한 테두리(선택)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_noti),
                                contentDescription = "Notification",
                                tint = Color.White,                 // 아이콘 흰색
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 3) 피해 신고 집중 접수 주소지 TOP: K/지 Placeholder
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "피해 신고 집중 접수 주소지 TOP : ",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
                // K/지 아이콘/텍스트 (Placeholder)
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)), // 연두색 배경
                    border = BorderStroke(1.dp, Color(0xFF4CAF50)) // 녹색 테두리
                ) {
                    Text(
                        "K",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = Color(0xFF4CAF50), // 녹색 텍스트
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.size(4.dp))
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCCBC)), // 살구색 배경
                    border = BorderStroke(1.dp, Color(0xFFF44336)) // 빨간색 테두리
                ) {
                    Text(
                        "지",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = Color(0xFFF44336), // 빨간색 텍스트
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 빈 영역 (내용 Placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // 적당한 높이 설정
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // 이 영역에 실제 피해 신고 TOP 리스트가 들어갈 것입니다.
                // Text("피해 신고 TOP 리스트 영역")
            }
        }
    }
}

// 하단 탭 항목 정의
sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Home : BottomNavItem("홈", R.drawable.ic_home, "main_screen")
    object Search : BottomNavItem("신고 탐색", R.drawable.ic_location, "map")
    object Risk : BottomNavItem("위험도 조회", R.drawable.ic_list, "search") // 가상의 경로
    object My : BottomNavItem("마이", R.drawable.ic_profile, "my_page") // 가상의 경로
}

// 하단 내비게이션 바 Composable
@Composable
fun BottomNavigationBar(navController: NavHostController) {
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