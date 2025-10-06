package com.zipcheck.android.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.White
import java.lang.Exception

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
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

@Composable
fun CustomTopBar(
    title: String = "",
    navController: NavHostController,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 8.dp)
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        // 뒤로 가기 버튼
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .padding(start = 4.dp)
                .clickable { navController.popBackStack() } // 클릭 시 이전 화면으로 돌아감
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