package com.zipcheck.android.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.*
import com.zipcheck.android.R
import com.zipcheck.android.data.api.KakaoLocalKeywordService
import com.zipcheck.android.data.api.KakaoLocalService
import com.zipcheck.android.data.api.MapService
import com.zipcheck.android.data.api.ReportService
import com.zipcheck.android.data.network.KakaoRetrofit
import com.zipcheck.android.data.network.RetrofitObj
import com.zipcheck.android.data.repo.MapRepository
import com.zipcheck.android.data.repo.ReportRepository
import com.zipcheck.android.ui.component.common.CustomTopBar
import com.zipcheck.android.ui.component.home.TypeBadge
import com.zipcheck.android.ui.component.risk.SearchBarOverlay
import com.zipcheck.android.ui.theme.BGGray
import com.zipcheck.android.ui.theme.BorderGray
import com.zipcheck.android.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 장소/주소 통합 검색 (서울시청 검색 가능)
 */
suspend fun geocodeSearch(query: String, kakao: KakaoLocalKeywordService): Pair<LatLng, String>? {
    if (query.isBlank()) return null
    return try {
        // 기존 KakaoLocalService의 엔드포인트를 keyword.json으로 사용한다고 가정하거나
        // 인터페이스를 수정해야 합니다.
        val res = kakao.geocode(query = query)
        val doc = res.documents.firstOrNull() ?: return null
        val lng = doc.x?.toDoubleOrNull() ?: return null
        val lat = doc.y?.toDoubleOrNull() ?: return null

        val finalAddress = if (!doc.roadAddress.isNullOrBlank()) {
            doc.roadAddress
        } else {
            doc.addressName ?: query
        }

        Log.d("Geocoding", "Success: $finalAddress")

        Pair(LatLng.from(lat, lng), finalAddress)
    } catch (e: Exception) {
        Log.e("Geocoding", "Fail: ${e.message}")
        null
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavHostController, accessToken: String) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // 상태 관리
    val mapView = remember { MapView(context) }
    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    val perms = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    var pinsLayer by remember { mutableStateOf<LabelLayer?>(null) }
    var query by remember { mutableStateOf("") }

    // 시트 및 오버레이 상태
    var showListSheet by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var showNoResultOverlay by remember { mutableStateOf(false) }
    var lastSearchQuery by remember { mutableStateOf("") }

    val listItems = remember { mutableStateListOf<ReportRepository.ReportUi>() }
    var detailItem by remember { mutableStateOf<ReportRepository.ReportUi?>(null) }
    val labelToAddr = remember { mutableMapOf<Label, String>() }

    // API 및 레포지토리
    val retrofit = remember { RetrofitObj.getRetrofit(context) }
    val mapRepo = remember { MapRepository(retrofit.create(MapService::class.java)) }
    val reportRepo = remember { ReportRepository(retrofit.create(ReportService::class.java)) }
    val kakaoRestApiKey = remember { context.getString(R.string.REST_API_KEY).trim() }
    val kakaoService = remember { KakaoRetrofit.getRetrofit(context, kakaoRestApiKey).create(KakaoLocalKeywordService::class.java) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 마커 갱신 로직
    fun refreshPins(center: LatLng, radiusMeters: Int = 5000) {
        scope.launch {
            try {
                val data = mapRepo.fetchAddrList(center.latitude, center.longitude, radiusMeters)
                pinsLayer?.let { layer ->
                    layer.removeAll()
                    labelToAddr.clear()
                    data.forEach { item ->
                        val pos = LatLng.from(item.latitude, item.longitude)
                        val label = layer.addLabel(LabelOptions.from(pos).setStyles(LabelStyle.from(R.drawable.marker)))
                        labelToAddr[label] = item.address
                    }
                }
            } catch (e: Exception) {
                Log.e("MapScreen", "Pin error: ${e.message}")
            }
        }
    }

    // 결과 없음 알림 자동 닫기
    LaunchedEffect(showNoResultOverlay) {
        if (showNoResultOverlay) {
            delay(3000)
            showNoResultOverlay = false
        }
    }

    // 최초 권한 요청
    LaunchedEffect(Unit) {
        if (!perms.allPermissionsGranted) perms.launchMultiplePermissionRequest()
    }

    Scaffold(
        containerColor = White,
        topBar = { CustomTopBar("탐색", navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // 1. 지도 영역
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { mapView })

            // 2. 검색창
            SearchBarOverlay(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    focusManager.clearFocus()
                    if (query.isBlank()) return@SearchBarOverlay

                    scope.launch {
                        val result = geocodeSearch(query, kakaoService)

                        if (result != null) {
                            val (latLng, finalAddress) = result

                            kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(latLng, 15))

                            // 검색 마커 표시
                            pinsLayer?.let { layer ->
                                layer.removeAll()
                                val style = LabelStyle.from(R.drawable.marker)
                                val label = layer.addLabel(LabelOptions.from(latLng).setStyles(style))

                                labelToAddr[label] = finalAddress
                            }

                            try {
                                val reports = reportRepo.fetchReportsByAddress(accessToken, finalAddress, 0, 10)
                                if (reports.isNotEmpty()) {
                                    listItems.clear()
                                    listItems.addAll(reports)
                                    showListSheet = true
                                    showNoResultOverlay = false
                                } else {
                                    Toast.makeText(context, "해당 지역에 등록된 신고가 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("MapScreen", "Fetch fail: ${e.message}")
                            }
                        } else {
                            lastSearchQuery = query
                            showNoResultOverlay = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(0.88f).padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // 3. 바텀시트 (복구됨)
            if (showListSheet) {
                ModalBottomSheet(onDismissRequest = { showListSheet = false }, sheetState = sheetState, containerColor = White) {
                    ReportListSheet(items = listItems) { clicked ->
                        detailItem = clicked
                        showDetailSheet = true
                        showListSheet = false
                    }
                }
            }

            if (showDetailSheet && detailItem != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showDetailSheet = false
                        if (listItems.size > 1) {
                            showListSheet = true
                        }
                    },
                    sheetState = sheetState,
                    containerColor = White
                ) {
                    ReportDetailSheet(detailItem!!)
                }
            }

            // 4. 결과 없음 오버레이
            if (showNoResultOverlay) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)) {
                    NoResultOverlay(lastSearchQuery)
                }
            }

            // 지도 생명주기 및 초기 설정
            DisposableEffect(perms.allPermissionsGranted) {
                if (perms.allPermissionsGranted) {
                    mapView.start(object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {}
                        override fun onMapError(e: Exception?) {}
                    }, object : KakaoMapReadyCallback() {
                        @SuppressLint("MissingPermission")
                        override fun onMapReady(map: KakaoMap) {
                            kakaoMap = map
                            val layer = map.labelManager?.layer
                            pinsLayer = layer

                            layer?.setClickable(true)

                            map.setOnLabelClickListener { _, _, clickedLabel ->
                                val addr = labelToAddr[clickedLabel] ?: return@setOnLabelClickListener false
                                Log.d("MapScreen", "마커 클릭됨: $addr")
                                scope.launch {
                                    try {
                                        val reports = reportRepo.fetchReportsByAddress(accessToken, addr, 0, 10)
                                        if (reports.size <= 1) {
                                            detailItem = reports.firstOrNull()
                                            showDetailSheet = detailItem != null
                                        } else {
                                            listItems.clear()
                                            listItems.addAll(reports)
                                            showListSheet = true
                                        }
                                    } catch (e: Exception) {
                                        Log.e("MapScreen", "Marker click error: ${e.message}")
                                    }
                                }
                                true
                            }

                            // 초기 위치 설정 (현재 위치)
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { last ->
                                val init = LatLng.from(last.latitude, last.longitude)
                                map.moveCamera(CameraUpdateFactory.newCenterPosition(init, 15))
                                refreshPins(init)
                            }
                        }
                    })
                }
                onDispose {
                    mapView.pause()
                }
            }
        }
    }
}

@Composable
fun NoResultOverlay(query: String) {
    Surface(
        color = Color(0xFF444C55).copy(alpha = 0.9f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "'$query'에 대한 검색 결과가 없습니다.",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReportDetailSheet(item: ReportRepository.ReportUi) {
    Column(
        Modifier.fillMaxWidth().padding(16.dp).background(White)
    ) {
        TypeBadge(item.chipText)
        Spacer(Modifier.height(8.dp))
        Text(item.addr, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Black)
        if (!item.addrDetail.isNullOrBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(item.addrDetail!!, fontSize = 13.sp, color = Color(0xFF7A7A7A))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("계약 형태", fontSize = 12.sp, color = Color(0xFF7A7A7A))
            Spacer(Modifier.weight(1f))
            Text(item.contractTypeText, fontSize = 13.sp)
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("계약 일자", fontSize = 12.sp, color = Color(0xFF7A7A7A))
            Spacer(Modifier.weight(1f))
            Text(item.contractDateText, fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))
        if (!item.content.isNullOrBlank()) Text(item.content!!, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
    }
}


@Composable
private fun ReportListSheet(
    items: List<ReportRepository.ReportUi>,
    onClick: (ReportRepository.ReportUi) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(White)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .pointerInput(item.reportId) {
                        detectTapGestures(onTap = { onClick(item) })
                    },
                colors = CardDefaults.cardColors(containerColor = White),
            ) {
                Column() {
                    TypeBadge(item.chipText)

                    Spacer(Modifier.height(8.dp))
                    Text(item.addr, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)

                    if (!item.addrDetail.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(item.addrDetail!!, fontSize = 12.sp, color = Color(0xFF7A7A7A))
                    }

                    Spacer(Modifier.height(10.dp))
                    // ⬇️ Row 임포트 필요: import androidx.compose.foundation.layout.Row
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("계약 일자", fontSize = 12.sp, color = Color(0xFF7A7A7A))
                        Spacer(Modifier.weight(1f))
                        Text(item.contractDateText, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(Modifier.fillMaxWidth(), color = BorderGray)
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}