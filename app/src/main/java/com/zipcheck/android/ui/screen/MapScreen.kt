package com.zipcheck.android.ui.screen

import android.Manifest
import android.R.attr.label
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.zipcheck.android.ui.theme.White
import java.lang.Exception
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder
import com.zipcheck.android.data.api.MapService
import com.zipcheck.android.data.model.map.AddrListItem
import com.zipcheck.android.data.network.RetrofitObj
import com.zipcheck.android.data.repo.MapRepository
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.component.SearchBarOverlay
import kotlinx.coroutines.launch
import com.zipcheck.android.R
import com.zipcheck.android.data.api.KakaoLocalService
import com.zipcheck.android.ui.network.KakaoRetrofit

suspend fun geocodeAddress(address: String, kakao: KakaoLocalService): LatLng? {
    if (address.isBlank()) return null
    return try {
        val res = kakao.geocode(query = address)
        val doc = res.documents.firstOrNull() ?: return null
        val lng = doc.x?.toDoubleOrNull() ?: return null // x=경도
        val lat = doc.y?.toDoubleOrNull() ?: return null // y=위도
        LatLng.from(lat, lng)
    } catch (e: Exception) {
        Log.e("Geocoding", "Kakao fail: ${e.message}", e)
        null
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 1. MapView 인스턴스를 remember로 유지
    val mapView = remember {
        MapView(context)
    }
    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val perms = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val kakaoRestApiKey = remember { context.getString(R.string.REST_API_KEY).trim() }

    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }

    val retrofit = remember {
        RetrofitObj.getRetrofit(context)  // <= 너의 기존 객체 사용
    }
    val mapService = remember { retrofit.create(MapService::class.java) }
    val mapRepo = remember { MapRepository(mapService) }

    val kakaoRetrofit = remember { KakaoRetrofit.getRetrofit(context, kakaoRestApiKey) }
    val kakaoService = remember { kakaoRetrofit.create(KakaoLocalService::class.java) }

    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    var pinsLayer by remember { mutableStateOf<LabelLayer?>(null) }

    // 백엔드 호출 → 지도에 반영
    fun refreshPins(center: LatLng, radiusMeters: Int = 5000) {
        scope.launch {
            try {
                val data = mapRepo.fetchAddrList(
                    lat = center.latitude,
                    lng = center.longitude,
                    radiusMeters = radiusMeters
                )

                val layer = pinsLayer ?: kakaoMap?.labelManager?.layer
                if (layer == null) {
                    Log.e("MapScreen", "Label layer is null. Skip drawing pins.")
                    toast("레이어 준비 중… 잠시 후 다시 시도")
                    return@launch
                }

                layer.removeAll()

                var added = 0
                data.forEach { item ->
                    // 필드명 모두 커버: lat/lng or latitude/longitude (문자/숫자 모두)
                    val lat = when {
                        item.latitude != null -> item.latitude
                        else -> null
                    }
                    val lng = when {
                        item.longitude != null -> item.longitude
                        else -> null
                    }

                    if (lat == null || lng == null) {
                        Log.w("MapScreen", "Skip item (no coords): $item")
                        return@forEach
                    }

                    val pos = LatLng.from(lat, lng)
                    val style = LabelStyle.from(R.drawable.marker)

                    val label = LabelOptions.from(pos).setStyles(style)
                    layer.addLabel(label)
                    added++
                }

                Log.d("MapScreen", "Fetched=${data.size}, Added=$added")
                toast("주변 신고 ${data.size}건")
            } catch (e: Exception) {
                Log.e("MapScreen", "addrList error: ${e.message}", e)
                toast("신고 위치 불러오기 실패")
            }
        }
    }


    // 1) 최초 진입 시(또는 해당 화면 재진입 시) 안전하게 권한 요청
    LaunchedEffect(perms.permissions) {
        if (!perms.allPermissionsGranted) {
            perms.launchMultiplePermissionRequest()
        }
    }

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("탐색", navController)
        }
    ) { paddingValues -> // Scafflod의 padding 값을 받습니다.

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 👈 Scafflod의 상단바 높이만큼 공간 확보
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView } // MapView 인스턴스 반환
            )

            SearchBarOverlay(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    focusManager.clearFocus()
                    if (query.isBlank()) return@SearchBarOverlay

                    scope.launch {
                        val latLng = geocodeAddress(query, kakaoService)
                        if (latLng != null) {
                            kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
                            refreshPins(latLng, radiusMeters = 5000) // 검색 위치 기준 요청
                        } else {
                            toast("주소를 찾을 수 없습니다.")
                        }
                    }

                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.88f)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .pointerInput(Unit) {
                        // `detectTapGestures`를 사용해 탭(터치)이 발생했을 때 키보드를 내립니다.
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            )

            // 2. DisposableEffect로 MapView 생명주기 관리
            DisposableEffect(perms.allPermissionsGranted) {
                if (perms.allPermissionsGranted) { // 위치 권한 부여된 경우
                    // 화면 진입 시 MapView 시작
                    mapView.start(
                        object : MapLifeCycleCallback() {
                            override fun onMapDestroy() {
                                Log.d("MapScreen", "Kakao Map Destroyed")
                            }

                            override fun onMapError(error: Exception?) {
                                Log.e("MapScreen", "Kakao Map Error: ${error?.message}", error)
                            }
                        },
                        object : KakaoMapReadyCallback() {
                            @SuppressLint("MissingPermission")
                            override fun onMapReady(map: KakaoMap) {
                                kakaoMap = map

                                // 위치 업데이트를 요청하고, 위치가 업데이트되면 맵을 이동시킵니다.
                                locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    1000L, 10f,
                                    object :
                                        LocationListener { // DisposableEffect 내에서 새로운 Listener를 정의하여 map을 캡처
                                        override fun onLocationChanged(location: Location) {
                                            val newPosition =
                                                LatLng.from(location.latitude, location.longitude)
                                            map.moveCamera(
                                                CameraUpdateFactory.newCenterPosition(
                                                    newPosition
                                                )
                                            )
                                            Log.d("Location", "위치 업데이트: $newPosition")
                                        }

                                        override fun onProviderEnabled(provider: String) {}
                                        override fun onProviderDisabled(provider: String) {}
                                    }
                                )

                                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { last ->
                                    val init = LatLng.from(last.latitude, last.longitude)
                                    map.moveCamera(CameraUpdateFactory.newCenterPosition(init))
                                    refreshPins(init, 5000)
                                }
                            }
                        }
                    )
                }

                // Composable이 화면에서 제거될 때 정리
                onDispose {
                    mapView.resume()
                    mapView.pause()
                }
            }

            // 3) 권한 없을 때는 UI만 보여주고, 버튼으로 재요청(자동 호출 금지!)
            if (!perms.allPermissionsGranted) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("지도와 현재 위치를 표시하려면 위치 권한이 필요합니다.")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { perms.launchMultiplePermissionRequest() }) {
                            Text("권한 허용하기")
                        }
                    }
                }
            }
        }
    }
}

fun addMarker(map: KakaoMap, position: LatLng) {
    val labelLayer = map.labelManager?.layer ?: return

    // 텍스트 라벨 스타일 (아이콘처럼도 가능)
    val style = LabelStyle.from(R.drawable.marker)

    val label = LabelOptions.from(position)
        .setStyles(style)

    // 실제 마커 생성
    labelLayer.addLabel(label)
}
