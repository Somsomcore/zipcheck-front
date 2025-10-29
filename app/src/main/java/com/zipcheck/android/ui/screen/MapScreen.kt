package com.zipcheck.android.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.component.SearchBarOverlay

private suspend fun geocodeAddress(address: String): LatLng? {
    // 🚨 실제 카카오 지오코딩 API 호출 로직이 들어가야 합니다.
    // 예: Retrofit을 이용해 'https://dapi.kakao.com/v2/local/search/address.json?query=...' 호출

    Log.d("API", "주소 지오코딩 요청: $address")

    // 네트워킹 지연을 시뮬레이션
    kotlinx.coroutines.delay(500)

    // 가상의 성공 응답 (경기도 구리시 경춘로 276번길 34 예시)
    return if (address.isNotEmpty()) {
        LatLng.from(37.6048, 127.1002) // 가상의 좌표
    } else {
        null
    }
}

// MapScreen 컴포저블 외부에 정의

// 🚨 주의: 실제 네트워크 통신 로직(Retrofit 등)으로 대체해야 합니다.
//private suspend fun fetchPinDataFromBackend(latLng: LatLng): List<MapPinData> {
//    // 1. 요청 객체 생성 (radiusMeters는 임의로 500m 설정)
//    val request = AddrListRequest(
//        lat = latLng.latitude,
//        lng = latLng.longitude,
//        radiusMeters = 500
//    )
//    Log.d("API", "백엔드 데이터 요청: ${request}")
//
//    // 2. 백엔드 통신 시뮬레이션
//    kotlinx.coroutines.delay(1000)
//
//    // 3. 가상 응답 데이터 (AddrListResponse.kt 구조 사용)
//    val simulatedResponse = AddrListResponse(
//        isSuccess = true,
//        code = "200",
//        result = AddrListResult(
//            locations = listOf(
//                AddrListItem(37.6049, 127.1002, "경기도 구리시 경춘로 276번길 34", 3),
//                AddrListItem(37.6000, 127.1000, "경기도 구리시 인창동 56-1", 1),
//                AddrListItem(37.6055, 127.1015, "경기도 구리시 수택동 123", 5)
//            )
//        )
//    )
//
//    // 4. AddrListItem을 MapPinData로 변환
//    return simulatedResponse.result.locations.mapIndexed { index, item ->
//        MapPinData(
//            id = index + 1,
//            latLng = LatLng.from(item.latitude, item.longitude),
//            address = item.address,
//            reportCount = item.reportCount,
//
//            // 이미지 UI에 필요한 가상 데이터
//            buildingName = if (index == 0) "힐스테이트 구리역 102동 1903호" else "빌딩명 ${index + 1}",
//            type = if (index == 0) "아파트 전세" else "오피스텔 월세",
//            contractDate = "2002.12.12",
//            deposit = "전세금"
//        )
//    }
//}
//
//// MapScreen 내부에 추가할 함수 (또는 MapScreen 밖 ViewModel/Helper에 구현)
//private fun drawMarkers(map: KakaoMap, markers: List<MapPinData>) {
//    // ⚠️ 경고: 핀을 새로 그릴 때마다 기존 핀을 지워야 중복되지 않습니다.
//    // KakaoMap의 Marker 객체를 사용하여 기존 Marker를 제거하는 로직이 필요하지만,
//    // 여기서는 간단하게 MarkerLayer를 사용한다고 가정합니다.
//
//    // 기존 Layer를 지우는 로직 (실제 KakaoMap SDK 구현 방식에 따라 다름)
//    // map.markerLayer.removeAllMarkers() // 예시 코드 (실제 API 확인 필요)
//
//    // 새 핀 그리기 로직 (각 핀 데이터에 대해)
//    // for (data in markers) {
//    //     val marker = Marker.builder()
//    //         .position(data.latLng)
//    //         .markerLayerKey("SEARCH_RESULTS") // 레이어 지정
//    //         .build()
//    //     map.markerLayer.addMarker(marker)
//    //
//    //     // 핀 클릭 이벤트 처리: 클릭 시 selectedPin 상태 업데이트
//    //     // marker.setOnMarkerClickListener { selectedPin = data }
//    // }
//
//    // 🚨 실제 Kakao Vector Map SDK를 사용하여 핀을 그리고 클릭 이벤트를 등록하는 코드로 대체해야 합니다.
//    // 여기서는 개념적인 구조만 보여드립니다.
//    Log.d("MapScreen", "Markers drawn: ${markers.size}")
//}

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

    var query by remember { mutableStateOf("") }

    // 1) 최초 진입 시(또는 해당 화면 재진입 시) 안전하게 권한 요청
    LaunchedEffect(perms.permissions) {
        if (!perms.allPermissionsGranted) {
            perms.launchMultiplePermissionRequest()
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("탐색", navController)
        }
    ) { paddingValues -> // Scafflod의 padding 값을 받습니다.

        // **모든 지도 및 UI 콘텐츠는 이 paddingValues를 Modifier에 적용해야 합니다.**
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
                    // TODO: 여기서 카카오 장소검색/지오코딩 호출 후
                    // 결과 좌표로 map.moveCamera(...) 하면 끝!
                    focusManager.clearFocus()
                    if (query.isBlank()) return@SearchBarOverlay

                    // ✅ 검색 로직 통합 시작
//                    scope.launch {
//                        // 1. 지오코딩 실행
//                        val latLng = geocodeAddress(query)
//
//                        if (latLng != null) {
//                            Log.d("Search", "지오코딩 성공: $latLng")
//
//                            // 2. 백엔드에서 데이터 가져오기
//                            val results = fetchPinDataFromBackend(latLng)
//
//                            // 3. UI 상태 업데이트
//                            searchResults = results
//                            selectedPin = null // 새 검색이므로 상세 카드 숨김
//
//                            // 4. 지도 이동 (LaunchedEffect가 처리하지만, 명시적으로 바로 이동시킬 수도 있습니다.)
//                            // kakaoMapInstance?.moveCamera(...)
//
//                        } else {
//                            Log.e("Search", "지오코딩 실패: 주소를 찾을 수 없습니다.")
//                            // 사용자에게 실패 알림 (예: Toast 또는 SnackBar)
//                        }
//                    }

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

                                // 초기 위치 가져오기
                                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                    ?.let { lastLoc ->
                                        map.moveCamera(
                                            CameraUpdateFactory.newCenterPosition(
                                                LatLng.from(
                                                    lastLoc.latitude,
                                                    lastLoc.longitude
                                                )
                                            )
                                        )
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
