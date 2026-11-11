package com.hyunsikyoo.chagokchagok.data.tts

import ai.pleos.playground.tts.TextToSpeech
import ai.pleos.playground.tts.constant.ErrorType
import ai.pleos.playground.tts.constant.Mode
import ai.pleos.playground.tts.listener.EventListener
import ai.pleos.playground.tts.listener.OnInitializeListener
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 들려주기 — Pleos TextToSpeech SDK, ON_DEVICE 모드(앱 등록 없이 쓸 수 있는 모드. 퍼미션 TTS_SERVICE).
 * 문장을 하나씩 읽고, 한 문장이 끝나면(onDone) 형광펜을 다음 문장으로 옮긴다.
 * 엔진 초기화에 실패하면 소리 없이 글자 수만큼 기다리는 시연 방식으로 대신 읽는다(화면 흐름은 같다).
 */
class PleosTtsNarrator(
    context: Context,
    private val scope: CoroutineScope,
    private val millisPerChar: Long = 110,
) : DiaryNarrator {

    override val available: Boolean = true

    private val _state = MutableStateFlow(NarrationState())
    override val state: StateFlow<NarrationState> = _state.asStateFlow()

    private val tts = TextToSpeech(context.applicationContext, Mode.ON_DEVICE)
    @Volatile private var ready = false

    private var sentences: List<String> = emptyList()
    /** 멈춘 뒤 늦게 오는 onDone을 무시하려고 재생마다 번호를 바꾼다 */
    private var session = 0
    private var ticker: Job? = null
    private var silentJob: Job? = null

    private val listener = object : EventListener {
        override fun onStart() = Unit
        override fun onReady() = Unit
        override fun onStop() = Unit
        override fun onUpdatedRms(rms: Double) = Unit
        override fun onDone() {
            val current = session
            scope.launch { if (current == session) next() }
        }
        override fun onError(error: ErrorType) {
            Log.w(TAG, "tts error $error")
            val current = session
            // 엔진이 못 읽으면 남은 문장은 소리 없이 형광펜만 옮긴다
            scope.launch { if (current == session) playSilently() }
        }
    }

    init {
        runCatching {
            tts.initialize(object : OnInitializeListener {
                override fun onSuccess() {
                    runCatching {
                        tts.setLocale(Locale.KOREA)
                        tts.addEventListener(listener)
                        ready = true
                    }.onFailure { Log.w(TAG, "tts setup", it) }
                }
                override fun onFailure(error: ErrorType) {
                    Log.w(TAG, "tts init failed $error — 소리 없이 시연 방식으로")
                }
            })
        }.onFailure { Log.w(TAG, "TextToSpeech 연결 실패 — 소리 없이 시연 방식으로", it) }
    }

    override fun play(key: String, sentences: List<String>) {
        stop()
        if (sentences.isEmpty()) return
        this.sentences = sentences
        session++
        _state.value = NarrationState(playing = true, sentenceIndex = 0, key = key)
        ticker = scope.launch {
            while (isActive) {
                delay(1_000)
                _state.update { it.copy(elapsedSec = it.elapsedSec + 1) }
            }
        }
        if (ready) speakCurrent() else playSilently()
    }

    private fun speakCurrent() {
        val i = _state.value.sentenceIndex ?: return
        runCatching { tts.speak(sentences[i], true) }.onFailure {
            Log.w(TAG, "speak", it)
            playSilently()
        }
    }

    private fun next() {
        val i = (_state.value.sentenceIndex ?: return) + 1
        if (i >= sentences.size) {
            stop()
            return
        }
        _state.update { it.copy(sentenceIndex = i) }
        speakCurrent()
    }

    /** TTS를 못 쓸 때: 글자 수에 비례한 시간만큼 형광펜만 옮긴다 */
    private fun playSilently() {
        silentJob?.cancel()
        silentJob = scope.launch {
            val start = _state.value.sentenceIndex ?: 0
            for (index in start until sentences.size) {
                _state.update { it.copy(sentenceIndex = index) }
                delay((sentences[index].length * millisPerChar).coerceAtLeast(900))
            }
            stop()
        }
    }

    override fun stop() {
        session++
        silentJob?.cancel()
        ticker?.cancel()
        if (_state.value.playing && ready) runCatching { tts.stop() }
        _state.value = NarrationState()
    }

    fun release() {
        stop()
        runCatching {
            tts.removeEventListener(listener)
            tts.release()
        }
    }

    private companion object {
        const val TAG = "PleosTtsNarrator"
    }
}
