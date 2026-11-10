package com.hyunsikyoo.chagokchagok.data.tts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 들려주기 상태 (W-D1). [sentenceIndex]는 지금 읽는 문장(형광펜 위치), [elapsedSec]은 버튼에 보이는 지난 시간.
 * [key]는 무엇을 읽는 중인지(일기 날짜 등) — 홈·달력·특별한 날의 들려주기 버튼이 자기 것만 멈춤 아이콘으로 바꾼다.
 */
data class NarrationState(
    val playing: Boolean = false,
    val sentenceIndex: Int? = null,
    val elapsedSec: Int = 0,
    val key: String? = null,
) {
    fun playing(key: String) = playing && this.key == key

    /** "0:12" */
    val elapsedLabel: String get() = "${elapsedSec / 60}:${"%02d".format(elapsedSec % 60)}"
}

/**
 * 일기 낭독. 실제 구현은 Pleos TextToSpeech SDK(앱 등록 후 사용 가능)로 바꾼다.
 * TTS를 못 쓰면 [available]이 false → 화면은 들려주기 대신 "폰에서 듣기"를 보여 준다.
 */
interface DiaryNarrator {
    val available: Boolean
    val state: StateFlow<NarrationState>
    fun play(key: String, sentences: List<String>)
    fun stop()
}

/**
 * 시연용 낭독기: 소리 없이 글자 수에 비례한 시간만큼 문장 형광펜을 옮기고 지난 시간을 센다.
 * Pleos TTS SDK 연결 전 화면 흐름(재생 → 문장 이동 → 끝나면 원래 버튼) 확인용.
 */
class TimedDemoNarrator(
    private val scope: CoroutineScope,
    private val millisPerChar: Long = 110,
) : DiaryNarrator {
    override val available: Boolean = true

    private val _state = MutableStateFlow(NarrationState())
    override val state: StateFlow<NarrationState> = _state.asStateFlow()
    private var job: Job? = null
    private var ticker: Job? = null

    override fun play(key: String, sentences: List<String>) {
        stop()
        _state.value = NarrationState(playing = true, sentenceIndex = 0, key = key)
        ticker = scope.launch {
            while (isActive) {
                delay(1_000)
                _state.update { it.copy(elapsedSec = it.elapsedSec + 1) }
            }
        }
        job = scope.launch {
            sentences.forEachIndexed { index, sentence ->
                _state.update { it.copy(sentenceIndex = index) }
                delay((sentence.length * millisPerChar).coerceAtLeast(900))
            }
            stop()
        }
    }

    override fun stop() {
        job?.cancel()
        ticker?.cancel()
        _state.value = NarrationState()
    }
}
