package com.example.trendborsa.data.remote.ws

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class TrendBorsaWebSocket(
    okHttpClient: OkHttpClient,
    private val json: Json,
    private val wsBaseUrl: String
) {
    private val wsClient = okHttpClient.newBuilder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<WsEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<WsEvent> = _events.asSharedFlow()

    private var webSocket: WebSocket? = null
    private var currentDropId: String? = null
    private var shouldReconnect = false

    fun connect(dropId: String) {
        shouldReconnect = true
        currentDropId = dropId
        doConnect(dropId)
    }

    private fun doConnect(dropId: String) {
        webSocket?.cancel()

        val url = "$wsBaseUrl/ws/drops/$dropId"
        Log.d(TAG, "Connecting to $url")

        val request = Request.Builder().url(url).build()
        webSocket = wsClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "Connected to drop=$dropId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val event = json.decodeFromString<WsEvent>(text)
                    Log.d(TAG, "Event: type=${event.type} price=${event.price} dir=${event.direction}")
                    _events.tryEmit(event)
                } catch (e: Exception) {
                    Log.w(TAG, "Parse error: ${e.message}, raw=$text")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                scheduleReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $reason")
                scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        if (!shouldReconnect) return
        val dropId = currentDropId ?: return
        scope.launch {
            delay(RECONNECT_DELAY_MS)
            if (shouldReconnect) {
                Log.d(TAG, "Reconnecting...")
                doConnect(dropId)
            }
        }
    }

    fun disconnect() {
        shouldReconnect = false
        currentDropId = null
        webSocket?.close(NORMAL_CLOSURE, "Client disconnect")
        webSocket = null
    }

    companion object {
        private const val TAG = "TrendBorsaWS"
        private const val NORMAL_CLOSURE = 1000
        private const val RECONNECT_DELAY_MS = 3000L
    }
}
