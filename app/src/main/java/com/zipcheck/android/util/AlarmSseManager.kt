package com.zipcheck.android.util

import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.HttpConnectStrategy // 추가 확인
import com.launchdarkly.eventsource.MessageEvent
import com.launchdarkly.eventsource.background.BackgroundEventHandler
import com.launchdarkly.eventsource.background.BackgroundEventSource
import okhttp3.Headers
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeUnit

class AlarmSseManager(
    private val accessToken: String,
    private val onMessageReceived: (String) -> Unit // 데이터를 전달받을 콜백 추가
) {

    private var eventSource: BackgroundEventSource? = null

    fun startSubscription() {
        val eventHandler = object : BackgroundEventHandler {
            override fun onOpen() {
                println("SSE 연결 성공")
            }

            override fun onMessage(event: String, messageEvent: MessageEvent) {
                // 수신된 데이터를 콜백을 통해 ViewModel로 전달
                onMessageReceived(messageEvent.data ?: "")
            }

            override fun onClosed() {
                println("SSE 연결 종료")
            }

            override fun onError(t: Throwable) {
                println("SSE 에러 발생: ${t.message}")
            }

            override fun onComment(comment: String) {}
        }

        val url = "http://192.168.219.195:8080/api/alarm/subscribe" // 실제 서버 주소
        val uri = URI.create(url)

        val connectStrategy = ConnectStrategy.http(uri)
            .headers(Headers.Builder()
                .add("Authorization", "Bearer $accessToken")
                .add("Accept", "text/event-stream")
                .build())
            .connectTimeout(10, TimeUnit.SECONDS)

        val eventSourceBuilder = EventSource.Builder(connectStrategy)
            .retryDelay(3, TimeUnit.SECONDS)

        eventSource = BackgroundEventSource.Builder(eventHandler, eventSourceBuilder)
            .build()

        eventSource?.start()
    }

    fun stopSubscription() {
        eventSource?.close()
        eventSource = null
    }
}