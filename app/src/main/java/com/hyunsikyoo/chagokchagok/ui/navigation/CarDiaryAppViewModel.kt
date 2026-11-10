package com.hyunsikyoo.chagokchagok.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hyunsikyoo.chagokchagok.data.demo.DemoDiaryRepository
import com.hyunsikyoo.chagokchagok.data.vehicle.GearState
import com.hyunsikyoo.chagokchagok.data.vehicle.VehicleSignals
import com.hyunsikyoo.chagokchagok.domain.model.FamilyMember
import com.hyunsikyoo.chagokchagok.domain.model.Highlight
import com.hyunsikyoo.chagokchagok.domain.model.HighlightType
import com.hyunsikyoo.chagokchagok.domain.model.Relation
import com.hyunsikyoo.chagokchagok.domain.model.Seat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/** 화면 (와이어프레임 W0 화면 연결 지도). 마일스톤(W5)은 11/8 범위 제외 */
sealed interface Screen {
    data object Home : Screen
    data class Diary(val date: LocalDate? = null) : Screen
    data object Calendar : Screen
    data object Settings : Screen
    data object Driving : Screen
}

/** 앱 창 위에 뜨는 팝업. 주행 중엔 전부 닫힌다 */
enum class AppPopup { None, Boarding, Arrival, SendPhone, SpecialDay }

/** 설정(W9) 값 — 목록 4줄로 확정(11/8): 차 이름, 가족과 자리, 특별한 날 알림, 오늘은 기록 안 하기 */
data class SettingsState(
    val carName: String = "소나",
    val family: List<FamilyMember> = DemoDiaryRepository.family,
    /** 지난번에 고른 자리. 탑승 팝업(W3)의 기본값 */
    val seats: Map<Seat, FamilyMember?> = DemoDiaryRepository.defaultSeats,
    val specialDayAlerts: Boolean = true,
    /** 오늘은 기록 안 하기. 주행 중 화면 "기록 끄기"와 같은 값, 자정 자동 복귀 */
    val recordingOffToday: Boolean = false,
    /** 시연 모드: 트립 종료 대기를 짧게, 주행 중 명장면을 시연 순서대로 쌓는다 (화면엔 없음) */
    val demoMode: Boolean = true,
) {
    /** 일기 주인공: 등록된 아이(여럿이면 번갈아 — 지금은 첫 아이) */
    val mainChild: FamilyMember? get() = family.firstOrNull { it.isChild }
}

/** 진행 중인 트립 (주행 중 화면) */
data class LiveTrip(
    val startAt: LocalDateTime,
    val fromPlace: String,
    val destination: String?,
    val eta: LocalDateTime?,
    val highlights: List<Highlight> = emptyList(),
    val resumed: Boolean = false,
)

/**
 * 앱 전체 흐름 — 와이어프레임 W0 "하루의 흐름"과 "자동 전환 규칙", 11/8 디자인 완료 핸드오프.
 * - 홈 [기록 시작] → 탑승(W3) → [이대로 출발] → 홈 "기록 준비됨"(W2-1) + Toast.
 * - P → D: 어떤 화면·팝업이든 주행 중(W1)으로. 탑승 팝업은 그 순간 값으로 확정, 도착 팝업은 "나중에".
 * - D → P: 주행 중 유지. 트립 종료(P 10분, 시연 모드 5초)일 때만 도착 팝업(W6/W6-1) → 일기.
 * - 뒷문 열림 + 정차 → 탑승 팝업(W3). 저장된 자리가 없으면 W3-0.
 */
class CarDiaryAppViewModel(app: Application) : AndroidViewModel(app) {

    private val signals = VehicleSignals(app)

    private val _stack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val stack: StateFlow<List<Screen>> = _stack.asStateFlow()

    private val _popup = MutableStateFlow(AppPopup.None)
    val popup: StateFlow<AppPopup> = _popup.asStateFlow()

    private val _settings = MutableStateFlow(SettingsState())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    private val _trip = MutableStateFlow<LiveTrip?>(null)
    val trip: StateFlow<LiveTrip?> = _trip.asStateFlow()

    /** 탑승 팝업에서 고치는 중인 자리 (확정 전) */
    private val _boardingSeats = MutableStateFlow(_settings.value.seats)
    val boardingSeats: StateFlow<Map<Seat, FamilyMember?>> = _boardingSeats.asStateFlow()

    /** 홈 헤더 "기록 준비됨"(W2-1) */
    private val _readyToRecord = MutableStateFlow(false)
    val readyToRecord: StateFlow<Boolean> = _readyToRecord.asStateFlow()

    /** 앱 영역 아래 가운데 Toast (3초) */
    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    /** 도착 팝업 자동 저장까지 남은 초 */
    private val _arrivalCountdown = MutableStateFlow(ARRIVAL_AUTO_SAVE_SEC)
    val arrivalCountdown: StateFlow<Int> = _arrivalCountdown.asStateFlow()
    /** 이름 붙이기(W6-2) 중엔 자동 저장을 멈춘다 */
    private var arrivalPaused = false

    private val _gear = MutableStateFlow(GearState.Unknown)
    /** 지금 기어 (시연 조작 패널 표시용) */
    val gear: StateFlow<GearState> = _gear.asStateFlow()

    /** 시연: 다음 트립을 내비 목적지 없이 시작(W6-1 확인용) */
    private val _nextTripWithoutDestination = MutableStateFlow(false)
    val nextTripWithoutDestination: StateFlow<Boolean> = _nextTripWithoutDestination.asStateFlow()

    private var tripEndJob: Job? = null
    private var arrivalJob: Job? = null
    private var demoScriptJob: Job? = null
    private var toastJob: Job? = null

    init {
        signals.start()
        viewModelScope.launch { signals.gear.drop(1).collect(::onGear) }
        viewModelScope.launch { signals.rearDoorOpened.collect { onRearDoorOpened() } }
        // 1년 전 오늘 일기가 있으면 홈을 열 때 하루 한 번 (W8)
        if (_settings.value.specialDayAlerts) _popup.value = AppPopup.SpecialDay
    }

    // ── 화면 이동 ─────────────────────────────────────────

    fun navigate(screen: Screen) {
        if (isDriving()) return // 주행 중엔 다른 화면이 열리지 않는다
        _stack.update { it + screen }
    }

    /** 뒤로. 홈이면 false (앱 밖으로) */
    fun back(): Boolean {
        val current = _stack.value
        if (current.size <= 1) return false
        _stack.value = current.dropLast(1)
        return true
    }

    fun openPopup(popup: AppPopup) {
        if (isDriving()) return
        if (popup == AppPopup.Boarding) _boardingSeats.value = _settings.value.seats
        _popup.value = popup
    }

    fun closePopup() {
        when (_popup.value) {
            AppPopup.Arrival -> finishArrival(place = null)
            else -> _popup.value = AppPopup.None
        }
    }

    private fun isDriving() = _stack.value.lastOrNull() == Screen.Driving

    fun showToast(message: String) {
        toastJob?.cancel()
        _toast.value = message
        toastJob = viewModelScope.launch {
            delay(3_000)
            _toast.value = null
        }
    }

    // ── 홈 [기록 시작] (W2 → W3 → W2-1) ───────────────────

    fun startRecording() {
        // "오늘은 기록 안 하기"가 켜져 있었다면 [기록 시작]이 그 설정을 끈다
        if (_settings.value.recordingOffToday) _settings.update { it.copy(recordingOffToday = false) }
        openPopup(AppPopup.Boarding)
    }

    // ── 차량 신호 ─────────────────────────────────────────

    /** 시연 신호와 실제 기어 신호가 같은 길로 들어온다 */
    fun onGear(gear: GearState) {
        _gear.value = gear
        when (gear) {
            GearState.Drive -> startOrContinueDriving()
            GearState.Park -> if (isDriving()) scheduleTripEnd()
            else -> Unit
        }
    }

    fun onRearDoorOpened() {
        // 정차 중 + 다른 팝업 없음 + 주행 중 화면 아님. 앞문만 열린 날(혼자 출근)은 신호가 오지 않는다
        if (_gear.value == GearState.Drive || isDriving() || _popup.value != AppPopup.None) return
        openPopup(AppPopup.Boarding)
    }

    fun demoNextTripWithoutDestination(on: Boolean = true) {
        _nextTripWithoutDestination.value = on
    }

    private fun startOrContinueDriving() {
        tripEndJob?.cancel()
        // 열려 있던 팝업 처리: 탑승은 현재 값으로 확정, 도착은 "나중에"
        when (_popup.value) {
            AppPopup.Boarding -> commitBoarding(showReady = false)
            AppPopup.Arrival -> finishArrival(place = null)
            else -> _popup.value = AppPopup.None
        }
        _readyToRecord.value = false
        if (_trip.value == null) {
            val now = LocalDateTime.now()
            val noDest = _nextTripWithoutDestination.value
            _nextTripWithoutDestination.value = false
            _trip.value = LiveTrip(
                startAt = now,
                fromPlace = "우리 집",
                destination = if (noDest) null else "할머니 댁", // NaviHelper 목적지 연결 전 시연 값
                eta = if (noDest) null else now.plusMinutes(100),
            )
            startDemoScript()
        }
        if (!isDriving()) _stack.update { it + Screen.Driving }
    }

    private fun scheduleTripEnd() {
        tripEndJob?.cancel()
        val waitMs = if (_settings.value.demoMode) DEMO_TRIP_END_MS else TRIP_END_MS
        tripEndJob = viewModelScope.launch {
            delay(waitMs)
            endTrip()
        }
    }

    /** 시연 "트립 바로 끝내기" */
    fun endTripNow() {
        if (_trip.value != null) endTrip()
    }

    private fun endTrip() {
        demoScriptJob?.cancel()
        val trip = _trip.value ?: return
        val recordingOff = _settings.value.recordingOffToday
        if (!recordingOff && trip.destination != null) {
            _trip.value = trip.copy(
                highlights = trip.highlights + Highlight(0, HighlightType.Arrival, LocalDateTime.now(), "${trip.destination} 도착", score = 100),
            )
        }
        when {
            recordingOff -> leaveDriving(toDiary = false) // 꺼진 날은 일기를 쓰지 않는다
            trip.destination == "우리 집" -> leaveDriving(toDiary = true) // 집·반복 길은 도착 팝업 없이
            else -> showArrival() // 목적지 없음이면 W6-1
        }
    }

    private fun showArrival() {
        _popup.value = AppPopup.Arrival
        _arrivalCountdown.value = ARRIVAL_AUTO_SAVE_SEC
        arrivalPaused = false
        arrivalJob?.cancel()
        arrivalJob = viewModelScope.launch {
            while (isActive && _arrivalCountdown.value > 0) {
                delay(1_000)
                if (!arrivalPaused) _arrivalCountdown.update { it - 1 }
            }
            // 30초 무반응: 내비 목적지가 있으면 그 값으로, 없으면 장소 없이(제목은 동네 이름)
            finishArrival(place = _trip.value?.destination)
        }
    }

    fun pauseArrivalCountdown(paused: Boolean) {
        arrivalPaused = paused
    }

    /** 도착 팝업: 맞아/나중에/자동 저장 모두 일기 화면으로. 장소는 [place] (null이면 장소 없이) */
    fun finishArrival(place: String?) {
        arrivalJob?.cancel()
        _popup.value = AppPopup.None
        leaveDriving(toDiary = true)
    }

    private fun leaveDriving(toDiary: Boolean) {
        _trip.value = null
        _stack.update { stack ->
            val withoutDriving = stack.filterNot { it == Screen.Driving }
            if (toDiary) withoutDriving + Screen.Diary(null) else withoutDriving
        }
    }

    private fun startDemoScript() {
        demoScriptJob?.cancel()
        if (!_settings.value.demoMode) return
        demoScriptJob = viewModelScope.launch {
            DemoDiaryRepository.drivingScript.forEach { highlight ->
                delay(DEMO_HIGHLIGHT_INTERVAL_MS)
                if (_settings.value.recordingOffToday) return@forEach
                _trip.update { it?.copy(highlights = it.highlights + highlight.copy(at = LocalDateTime.now())) }
            }
        }
    }

    // ── 주행 중 기록 끄기 (W1-1) ─────────────────────────

    fun toggleRecordingWhileDriving() {
        val off = !_settings.value.recordingOffToday
        _settings.update { it.copy(recordingOffToday = off) }
        if (off) showToast("홈에서도 다시 켤 수 있어요")
    }

    // ── 탑승 팝업 (W3, W3-0, W3-1, W3-2) ─────────────────

    fun setBoardingSeat(seat: Seat, member: FamilyMember?) {
        _boardingSeats.update { seats -> seats.seatOnly(seat, member) }
    }

    /** [이대로 출발] */
    fun confirmBoarding() = commitBoarding(showReady = true)

    private fun commitBoarding(showReady: Boolean) {
        // 지난번에 고른 자리가 다음 탑승의 기본값
        _settings.update { it.copy(seats = _boardingSeats.value) }
        _popup.value = AppPopup.None
        if (showReady) {
            _readyToRecord.value = true
            showToast("출발하면 ${_settings.value.carName}가 기록을 시작해요")
        }
    }

    fun skipRecordingToday() {
        _settings.update { it.copy(recordingOffToday = true) }
        _readyToRecord.value = false
        _popup.value = AppPopup.None
    }

    // ── 가족 (W3-2 새 가족 추가, W9-2 가족 고치기) ────────

    fun addMember(name: String, relation: Relation, birthday: LocalDate?): FamilyMember {
        val id = (_settings.value.family.maxOfOrNull { it.id } ?: 0) + 1
        val member = FamilyMember(id, name.trim(), relation, birthday)
        _settings.update { it.copy(family = it.family + member) }
        return member
    }

    fun updateMember(member: FamilyMember) {
        _settings.update { s ->
            s.copy(
                family = s.family.map { if (it.id == member.id) member else it },
                seats = s.seats.mapValues { (_, m) -> if (m?.id == member.id) member else m },
            )
        }
        _boardingSeats.update { seats -> seats.mapValues { (_, m) -> if (m?.id == member.id) member else m } }
    }

    /** 지우면 그 사람의 자리만 비고 지난 일기는 남는다 */
    fun deleteMember(member: FamilyMember) {
        _settings.update { s ->
            s.copy(
                family = s.family.filterNot { it.id == member.id },
                seats = s.seats.mapValues { (_, m) -> if (m?.id == member.id) null else m },
            )
        }
    }

    // ── 설정 (W9) ────────────────────────────────────────

    fun setCarName(name: String) = _settings.update { it.copy(carName = name.trim()) }
    fun setSpecialDayAlerts(on: Boolean) = _settings.update { it.copy(specialDayAlerts = on) }
    fun setRecordingOffToday(off: Boolean) = _settings.update { it.copy(recordingOffToday = off) }
    fun setSeat(seat: Seat, member: FamilyMember?) = _settings.update { it.copy(seats = it.seats.seatOnly(seat, member)) }

    override fun onCleared() {
        signals.stop()
    }

    /** 한 사람은 한 자리에만: 이미 다른 자리에 앉은 사람을 고르면 그 자리는 비운다 */
    private fun Map<Seat, FamilyMember?>.seatOnly(seat: Seat, member: FamilyMember?): Map<Seat, FamilyMember?> =
        mapValues { (s, m) -> if (member != null && m?.id == member.id && s != seat) null else m } + (seat to member)

    private companion object {
        const val TRIP_END_MS = 10 * 60 * 1_000L
        const val DEMO_TRIP_END_MS = 5_000L
        const val DEMO_HIGHLIGHT_INTERVAL_MS = 6_000L
        const val ARRIVAL_AUTO_SAVE_SEC = 30
    }
}
