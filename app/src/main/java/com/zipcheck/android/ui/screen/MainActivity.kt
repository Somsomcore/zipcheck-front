package com.zipcheck.android.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zipcheck.android.data.model.riskAnalysis.RiskAnalysisResult
import com.zipcheck.android.ui.component.BottomNavigationBar
import com.zipcheck.android.ui.component.RiskAnalysisList
import com.zipcheck.android.ui.component.SearchBarOverlay
import com.zipcheck.android.ui.component.TopReportsCarousel
import com.zipcheck.android.ui.theme.BGGray
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.ExampleTextGray
import com.zipcheck.android.ui.theme.HomeBG
import com.zipcheck.android.ui.theme.HomeBGLinear0
import com.zipcheck.android.ui.theme.HomeBGLinear1
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
                        startDestination = "register_screen4", //main_screen
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        //LoginScreen route
                        composable("login_screen") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            LoginScreen(navController = navController)
                        }
                        //LoginScreen_name route
                        composable("login_screen_name") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            NameInputScreen(navController = navController)
                        }
                        composable(
                            route = "login_screen_telecom/{name}",
                            arguments = listOf(navArgument("name") { type = NavType.StringType })
                        ) { backStackEntry ->
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
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
                            LaunchedEffect(Unit) {
                                showBottomBar.value = true
                            }
                            MainScreen(navController = navController)
                        }
                        composable("risk_analysis_record") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            RiskAnalysisRecordScreen(navController = navController)
                        }
                        composable("risk_analysis_result") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            RiskAnalysisResultScreen(navController = navController)
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
                                showBottomBar.value = false
                            }
                            RegisterScreen(navController = navController)
                        }
                        composable(
                            route = "register_screen_2/{address}/{detailAddress}",
                            arguments = listOf(
                                navArgument("address") { type = NavType.StringType },
                                navArgument("detailAddress") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            // RegisterScreen2는 BottomBar를 숨겨야 하므로 추가
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }

                            val address = backStackEntry.arguments?.getString("address") ?: ""
                            val detailAddress = backStackEntry.arguments?.getString("detailAddress") ?: ""

                            RegisterScreen2(
                                navController = navController,
                                address = address,
                                detailAddress = detailAddress
                            )
                        }


                        composable(
                            // 1. 라우트를 'register_screen_3'으로 하고, 하나의 인자(allDataJson)를 받도록 수정합니다.
                            route = "register_screen_3/{allDataJson}",
                            arguments = listOf(
                                navArgument("allDataJson") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }

                            // 2. 전달받은 인코딩된 JSON 문자열을 가져옵니다.
                            val encodedJson = backStackEntry.arguments?.getString("allDataJson") ?: ""

                            // 3. 디코딩하여 원래의 JSON 문자열로 복원합니다.
                            val jsonString = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.name())

                            // 4. JSON 문자열을 다시 Map 객체로 변환합니다.
                            val gson = Gson()
                            val mapType = object : TypeToken<Map<String, String?>>() {}.type
                            val previousDataMap: Map<String, String?> = gson.fromJson(jsonString, mapType)

                            // 5. 복원된 Map 데이터를 RegisterScreen3에 전달합니다.
                            RegisterScreen3(
                                navController = navController,
                                previousData = previousDataMap
                            )
                        }
                        composable("register_screen4") {
                            RegisterScreen4(navController = navController)
                        }
                        composable("my_page") {
                            LaunchedEffect(Unit) {
                                showBottomBar.value = false
                            }
                            MyPageScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier

) {
    val homeBGLinear0 = HomeBGLinear0
    val homeBGLinear1 = HomeBGLinear1

    var query by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val tabs = listOf("아파트", "오피스텔", "빌라")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BGGray)
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
                    .height(230.dp)
                    .clickable(onClick = {navController.navigate("search")}),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                // Row 대신 Box로 배치 자유도 확보
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(listOf(homeBGLinear0, homeBGLinear1))
                    )
                ) {

                    // 좌측 텍스트
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 20.dp, top = 56.dp, end = 20.dp),
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
                                "전세 위험도 확인하러 가기 ", // > 문자 제거
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
                            .padding(bottom = 24.dp)
                            .size(140.dp),
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_noti),
                                contentDescription = "Notification",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }

            SearchBarOverlay(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    // TODO: 여기서 카카오 장소검색/지오코딩 호출 후
                    // 결과 좌표로 map.moveCamera(...) 하면 끝!
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 24.dp)       // 겹치는 정도 조절 (원하는 만큼 +/−)
                    .zIndex(1f)              // 배너 위에 보이도록
                    .fillMaxWidth(0.9f)
                    .pointerInput(Unit) {
                        // `detectTapGestures`를 사용해 탭(터치)이 발생했을 때 키보드를 내립니다.
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.logo_zipcheck),
                        contentDescription = "로고",
                        modifier = Modifier.size(39.dp),
                        tint = Color.Unspecified
                    )
                },
                trailingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_home_search),
                        contentDescription = "지우기",
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            )
        }

        Spacer(Modifier.height(50.dp))

        // 3) 피해 신고 집중 접수 주소지 TOP: K/지 Placeholder
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "피해 신고 집중 접수 주소지 TOP 5",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(Modifier.height(16.dp))

            TopReportsCarousel(
                tabs = listOf("아파트", "오피스텔", "빌라", "4", "5"),
                autoScrollMillis = 3500L // 자동 넘김 주기
            )
        }

        Spacer(Modifier.height(32.dp))

        // 신고 등록 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable(onClick = { navController.navigate("register") }),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                // Row 대신 Box로 배치 자유도 확보
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            HomeBG
                        )
                ) {

                    // 좌측 텍스트
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 20.dp, top = 0.dp, bottom = 0.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "나쁜 경험은 함께 나누고",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            lineHeight = 14.sp
                        )
                        Text(
                            "위험한 계약은 미리 피할 수 있도록",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            lineHeight = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "3분 만에 내 경험 공유하고, 신고 등록하러 가기 ", // > 문자 제거
                                style = MaterialTheme.typography.bodySmall,
                                color = ExampleTextGray,
                                fontSize = 10.sp
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_next),
                                contentDescription = "Go",
                                tint = ExampleTextGray,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    // ✅ 집 이미지는 Image로, 하단-오른쪽에 적당한 크기로
                    Image(
                        painter = painterResource(id = R.drawable.img_danger),
                        contentDescription = "House",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(top = 0.dp, bottom = 0.dp)
                            .size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "최근 실행한 위험도 분석",
                    style = MaterialTheme.typography.titleMedium,
                    color = Black
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = "next",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { navController.navigate("risk_analysis_record") } // 클릭 시 이전 화면으로 돌아감
                )
            }

        }

        val sampleResults = listOf(
            RiskAnalysisResult(
                id = 1,
                address = "경기도 구리시 인창2로 65 (인창동)",
                apartment = "힐스테이트 구리역 105동 1604호",
                riskPercentage = 88,
                riskLevel = "아주 위험",
                note = "유사 매물 대비 보증금이 10% 높습니다",
                date = LocalDate.of(2025, 9, 14)
            ),
            RiskAnalysisResult(
                id = 2,
                address = "경기도 구리시 인창2로 65 (인창동)",
                apartment = "힐스테이트 구리역 105동 1604호",
                riskPercentage = 60,
                riskLevel = "의심",
                note = "유사 매물 대비 보증금이 10% 높습니다",
                date = LocalDate.of(2025, 9, 14)
            )
            // 여기에 추가 결과들을 넣을 수 있습니다.
        )

        // case 1: 결과 2개 + 추가 카드 1개
        RiskAnalysisList(
            results = sampleResults,
            onAddClicked = { navController.navigate("search_") },
            onItemClicked = { result -> println("Clicked: ${result.address}") },
            navController = navController
        )
    }
}