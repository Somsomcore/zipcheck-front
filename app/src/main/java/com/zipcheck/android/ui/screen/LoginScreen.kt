package com.zipcheck.android.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.zipcheck.android.R
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.zipcheck.android.data.api.AuthService
import com.zipcheck.android.data.api.SocialLoginRequest
import com.zipcheck.android.data.api.SocialLoginResponse
import com.zipcheck.android.data.network.RetrofitObj
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val service = RetrofitObj.getRetrofit(context).create(AuthService::class.java)
    // NameInputScreen으로부터 결과를 받아서 처리하는 부분
    val signupResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("signup_result_key")?.observeAsState()

    LaunchedEffect(signupResult?.value) {
        if (signupResult?.value == "success") {
            scope.launch {
                snackbarHostState.showSnackbar("회원가입이 완료되었습니다. 로그인을 진행해 주세요.")
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("signup_result_key")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 뒤로가기 아이콘
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 로고
            Image(
                painter = painterResource(id = R.drawable.login_icon),
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 로그인 / 회원가입 텍스트 + 구분선
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray, thickness = 1.dp)
                Text(
                    text = "로그인 / 회원가입",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Divider(modifier = Modifier.weight(1f), color = Color.Gray, thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 카카오 로그인 버튼
            Button(
                onClick = {
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                            if (error != null) {
                                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                    Toast.makeText(context, "로그인이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "카카오 로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else if (token != null) {
                                handleKakaoLogin(token.accessToken, service, context, navController)
                            }
                        }
                    } else {
                        loginWithKakaoAccount(context, service, navController)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp), // 이미지와 비슷한 둥근 모서리
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE500), // 카카오 공식 노란색
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(0.dp) // 내부 여백 초기화
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_kakao),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified // 아이콘 원래 색상 유지
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "카카오 로그인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 네이버 로그인 버튼
            Button(
                onClick = {
                    NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
                        override fun onSuccess() {
                            val accessToken = NaverIdLoginSDK.getAccessToken()
                            if (accessToken != null) {
                                handleNaverLogin(accessToken, service, context, navController)
                            } else {
                                Toast.makeText(context, "토큰을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            Toast.makeText(context, "네이버 로그인 실패: $message", Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(errorCode: Int, message: String) {
                            Toast.makeText(context, "네이버 로그인 에러: $message", Toast.LENGTH_SHORT).show()
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03C75A), // 네이버 공식 초록색
                    contentColor = Color.White // 네이버는 글자가 흰색일 때 가장 잘 보입니다
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_naver),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp), // 네이버 로고 비율에 맞춰 조정
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "네이버 로그인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(2f)) // 화면 하단과 버튼 사이 여유 공간
        }
    }
}

// 카카오 계정(웹)으로 로그인하는 공통 처리 함수
private fun loginWithKakaoAccount(
    context: Context,
    service: AuthService,
    navController: NavController
) {
    UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
        if (error != null) {
            Toast.makeText(context, "카카오 로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            handleKakaoLogin(token.accessToken, service, context, navController)
        }
    }
}
// 카카오 로그인 성공 시 서버 로그인 호출
private fun handleKakaoLogin(
    accessToken: String,
    service: AuthService,
    context: Context,
    navController: NavController
) {
    val request = SocialLoginRequest(provider = "KAKAO", accessToken = accessToken)

    service.socialLogin(request)  // ✅ 헤더 파라미터 제거
        .enqueue(object : retrofit2.Callback<SocialLoginResponse> {
        override fun onResponse(call: Call<SocialLoginResponse>, response: Response<SocialLoginResponse>) {
            if (!response.isSuccessful) {
                Toast.makeText(context, "응답 오류", Toast.LENGTH_SHORT).show()
                return
            }
            val body = response.body()
            if (body?.isSuccess == true && body.result != null) {
                val login = body.result

                // ✅ 서버 발급 토큰 저장 (이 토큰으로 인증 API 호출)
                val shared = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                with(shared.edit()) {
                    putString("accessToken", login.accessToken)
                    putString("refreshToken", login.refreshToken)
                    apply()
                }

                val user = login.user
                if (user.name.isNullOrBlank()) {
                    navController.navigate("main_screen")
                } else {
                    Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                    navController.navigate("main_screen") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(context, "로그인 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<SocialLoginResponse>, t: Throwable) {
            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

private fun handleNaverLogin(
    accessToken: String,
    service: AuthService,
    context: Context,
    navController: NavController
) {
    val request = SocialLoginRequest(provider = "NAVER", accessToken = accessToken)
    service.socialLogin(request)  // ✅ 헤더 파라미터 제거
        .enqueue(object : retrofit2.Callback<SocialLoginResponse> {
        override fun onResponse(call: Call<SocialLoginResponse>, response: Response<SocialLoginResponse>) {
            if (!response.isSuccessful) {
                showHttpDebugToast(context, response, "AUTH")
                return
            }
            val body = response.body()
            if (body?.isSuccess == true && body.result != null) {
                val login = body.result
                val shared = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                with(shared.edit()) {
                    putString("accessToken", login.accessToken)
                    putString("refreshToken", login.refreshToken)
                    apply()
                }

                val user = login.user
                if (user.name.isNullOrBlank()) {
                    navController.navigate("login_screen_name")
                } else {
                    Toast.makeText(context, "네이버 로그인 성공!", Toast.LENGTH_SHORT).show()
                    navController.navigate("main_screen") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(context, "로그인 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<SocialLoginResponse>, t: Throwable) {
            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
private fun showHttpDebugToast(context: Context, response: Response<*>, tag: String) {
    val code = response.code()
    val msg = response.message()
    val err = try { response.errorBody()?.string()?.take(500) } catch (_: Throwable) { null }
    Toast.makeText(
        context,
        "[$tag] HTTP $code $msg\n${err ?: "(no error body)"}",
        Toast.LENGTH_LONG
    ).show()
}
