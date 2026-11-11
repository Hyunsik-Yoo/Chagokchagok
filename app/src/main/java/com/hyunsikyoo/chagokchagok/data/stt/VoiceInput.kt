package com.hyunsikyoo.chagokchagok.data.stt

import ai.pleos.playground.stt.SpeechToText
import ai.pleos.playground.stt.constant.ErrorType
import ai.pleos.playground.stt.constant.Mode
import ai.pleos.playground.stt.listener.OnInitializeListener
import ai.pleos.playground.stt.listener.ResultListener
import android.content.Context
import android.util.Log
import android.os.Handler
import android.os.Looper
import java.util.Locale

/**
 * 아이가 장소 이름을 말로 붙이기 (W6-2) — Pleos SpeechToText SDK, ON_DEVICE 모드(앱 등록 없이, 퍼미션 STT_SERVICE).
 * [listen]은 한 번 듣고 결과 하나를 돌려준다: 받아 적은 글, 또는 못 알아들었으면 null.
 * 엔진을 못 쓰면 [available]이 false — 화면은 시연 값으로 대신한다.
 */
class VoiceInput(context: Context) {

    private val stt = SpeechToText(context.applicationContext, Mode.ON_DEVICE)
    private val main = Handler(Looper.getMainLooper())

    @Volatile var available = false
        private set

    private var pending: ((String?) -> Unit)? = null
    private var lastPartial: String? = null

    private val listener = object : ResultListener {
        override fun onUpdated(text: String, isFinal: Boolean) {
            val t = text.trim()
            if (t.isNotEmpty()) lastPartial = t
            if (isFinal) finish(t.ifEmpty { null })
        }
        override fun onUpdatedEpdData(start: Long, end: Long) = Unit
        override fun onStartedRecognition() = Unit
        override fun onEndedRecognition() { finish(lastPartial) }
        override fun onError(error: ErrorType) {
            Log.w(TAG, "stt error $error")
            finish(null)
        }
        override fun onReady() = Unit
        override fun onUpdatedRms(rms: Double) = Unit
    }

    init {
        runCatching {
            stt.initialize(object : OnInitializeListener {
                override fun onSuccess() {
                    runCatching {
                        stt.setLocale(Locale.KOREA)
                        stt.addListener(listener)
                        available = true
                    }.onFailure { Log.w(TAG, "stt setup", it) }
                }
                override fun onFailure(error: ErrorType) { Log.w(TAG, "stt init failed $error") }
            })
        }.onFailure { Log.w(TAG, "SpeechToText 연결 실패", it) }
    }

    fun listen(onResult: (String?) -> Unit) {
        cancel()
        if (!available) {
            onResult(null)
            return
        }
        pending = onResult
        lastPartial = null
        // 아무 말이 없으면 끝나지 않으므로 10초 뒤 들은 데까지(없으면 null)로 끝낸다
        main.postDelayed(timeout, LISTEN_TIMEOUT_MS)
        runCatching { stt.request() }.onFailure {
            Log.w(TAG, "request", it)
            finish(null)
        }
    }

    private val timeout = Runnable {
        if (pending != null) {
            runCatching { stt.stop() }
            finish(lastPartial)
        }
    }

    fun cancel() {
        main.removeCallbacks(timeout)
        if (pending != null) runCatching { stt.stop() }
        pending = null
    }

    private fun finish(text: String?) {
        val cb = pending ?: return
        pending = null
        main.removeCallbacks(timeout)
        main.post { cb(text) }
    }

    fun release() {
        cancel()
        runCatching {
            stt.removeListener(listener)
            stt.release()
        }
    }

    private companion object {
        const val TAG = "VoiceInput"
        const val LISTEN_TIMEOUT_MS = 10_000L
    }
}
