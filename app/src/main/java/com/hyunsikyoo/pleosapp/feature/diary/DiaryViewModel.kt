package com.hyunsikyoo.pleosapp.feature.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunsikyoo.pleosapp.data.demo.DemoDiaryRepository
import com.hyunsikyoo.pleosapp.data.tts.DiaryNarrator
import com.hyunsikyoo.pleosapp.data.tts.TimedDemoNarrator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** 열려 있는 팝업 */
enum class DiaryPopup { None, Records, Pieces }

class DiaryViewModel : ViewModel() {

    private val diaries = DemoDiaryRepository.diaries()
    private val narrator: DiaryNarrator = TimedDemoNarrator(viewModelScope)

    private val latestIndex = diaries.indexOfFirst { it.date == DemoDiaryRepository.latest().date }.coerceAtLeast(0)
    private val index = MutableStateFlow(latestIndex)
    private val _popup = MutableStateFlow(DiaryPopup.None)
    val popup: StateFlow<DiaryPopup> = _popup

    val state: StateFlow<DiaryUiState> = combine(index, narrator.state) { i, narration ->
        diaries[i].toUiState(
            mileage = DemoDiaryRepository.mileage(),
            narration = narration,
            ttsAvailable = narrator.available,
            hasPrev = i > 0,
            hasNext = i < diaries.lastIndex,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        diaries[latestIndex].toUiState(
            DemoDiaryRepository.mileage(), narrator.state.value, narrator.available,
            hasPrev = latestIndex > 0, hasNext = latestIndex < diaries.lastIndex,
        ),
    )

    /** 홈·달력·마일스톤에서 고른 날의 일기 (null이면 가장 최근) */
    fun select(date: java.time.LocalDate?) {
        val target = date?.let { d -> diaries.indexOfFirst { it.date == d } }?.takeIf { it >= 0 } ?: latestIndex
        if (target != index.value) {
            narrator.stop()
            index.value = target
        }
    }

    /** 화면 이동 없이 소리만 (홈 "들려주기") */
    fun play(diary: com.hyunsikyoo.pleosapp.domain.model.Diary) {
        if (narrator.state.value.playing(diary.date.toString())) {
            narrator.stop()
            return
        }
        narrator.play(diary.date.toString(), diary.sentences + listOfNotNull(diary.phoneNote?.let { "엄마가 폰에서 남긴 말. $it" }))
    }

    /** 홈·달력·특별한 날 버튼이 읽는 중 표시를 고를 때 */
    val narration = narrator.state

    fun showPrev() = move(-1)
    fun showNext() = move(+1)

    private fun move(delta: Int) {
        val target = (index.value + delta).coerceIn(0, diaries.lastIndex)
        if (target != index.value) {
            narrator.stop()
            index.value = target
        }
    }

    /** 들려주기 탭: 재생 중이면 정지. 폰에서 덧붙인 한마디는 일기 글 끝에 이어 읽는다 */
    fun toggleNarration() {
        if (narrator.state.value.playing) {
            narrator.stop()
        } else {
            val diary = diaries[index.value]
            narrator.play(diary.date.toString(), diary.sentences + listOfNotNull(diary.phoneNote?.let { "엄마가 폰에서 남긴 말. $it" }))
        }
    }

    /** 명장면 팝업의 "이 순서로 들려주기" */
    fun narrateRecords() {
        _popup.value = DiaryPopup.None
        narrator.play("records", diaries[index.value].records.sortedBy { it.at }.map { it.title })
    }

    fun openPopup(popup: DiaryPopup) { _popup.value = popup }
    fun closePopup() { _popup.value = DiaryPopup.None }

    override fun onCleared() {
        narrator.stop()
    }
}
