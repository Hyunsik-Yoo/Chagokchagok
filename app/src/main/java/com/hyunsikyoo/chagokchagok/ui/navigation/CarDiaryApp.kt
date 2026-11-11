package com.hyunsikyoo.chagokchagok.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunsikyoo.chagokchagok.data.demo.DemoDiaryRepository
import com.hyunsikyoo.chagokchagok.data.vehicle.GearState
import com.hyunsikyoo.chagokchagok.feature.calendar.CalendarScreen
import com.hyunsikyoo.chagokchagok.feature.diary.DiaryRoute
import com.hyunsikyoo.chagokchagok.feature.diary.DiaryViewModel
import com.hyunsikyoo.chagokchagok.feature.driving.DrivingScreen
import com.hyunsikyoo.chagokchagok.feature.driving.DrivingUiState
import com.hyunsikyoo.chagokchagok.feature.home.HomeScreen
import com.hyunsikyoo.chagokchagok.feature.popups.ArrivalPopup
import com.hyunsikyoo.chagokchagok.feature.popups.BoardingPopup
import com.hyunsikyoo.chagokchagok.feature.popups.SendPhonePopup
import com.hyunsikyoo.chagokchagok.feature.popups.SpecialDayPopup
import com.hyunsikyoo.chagokchagok.feature.settings.SettingsScreen
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryToast
import java.time.LocalDateTime

/**
 * 앱 루트: 지금 화면 + 그 위 팝업 + Toast. 화면 연결은 와이어프레임 W0 "화면 연결 지도"와 11/8 핸드오프 그대로.
 * [onLeaveApp]: 홈에서 뒤로, 또는 주행 중 뒤로(홈 대신 Pleos 앱 목록으로).
 * [demoControls]: 디버그 빌드에서만 true. 왼쪽 아래 구석을 길게 누르면 시연 조작 패널.
 */
@Composable
fun CarDiaryApp(
    onLeaveApp: () -> Unit,
    demoControls: Boolean = false,
    app: CarDiaryAppViewModel = viewModel(),
    diaryViewModel: DiaryViewModel = viewModel(),
) {
    val stack by app.stack.collectAsStateWithLifecycle()
    val popup by app.popup.collectAsStateWithLifecycle()
    val settings by app.settings.collectAsStateWithLifecycle()
    val trip by app.trip.collectAsStateWithLifecycle()
    val boardingSeats by app.boardingSeats.collectAsStateWithLifecycle()
    val arrivalCountdown by app.arrivalCountdown.collectAsStateWithLifecycle()
    val readyToRecord by app.readyToRecord.collectAsStateWithLifecycle()
    val toast by app.toast.collectAsStateWithLifecycle()
    val diaryState by diaryViewModel.state.collectAsStateWithLifecycle()
    val narration by diaryViewModel.narration.collectAsStateWithLifecycle()
    val gear by app.gear.collectAsStateWithLifecycle()
    val noDestinationNext by app.nextTripWithoutDestination.collectAsStateWithLifecycle()
    var demoPanelOpen by rememberSaveable { mutableStateOf(false) }

    val screen = stack.last()
    val today = DemoDiaryRepository.today
    val childName = settings.mainChild?.name ?: "우리 아이"

    BackHandler {
        when {
            demoPanelOpen -> demoPanelOpen = false
            popup != AppPopup.None -> app.closePopup()
            screen == Screen.Driving -> onLeaveApp()
            !app.back() -> onLeaveApp()
        }
    }

    Box(Modifier.fillMaxSize()) {
        when (screen) {
            Screen.Home -> HomeScreen(
                carName = settings.carName,
                childName = childName,
                readyToRecord = readyToRecord,
                narration = narration,
                onStartRecording = app::startRecording,
                onOpenDiary = { d -> app.navigate(Screen.Diary(d?.date)) },
                onPlay = diaryViewModel::play,
                onOpenCalendar = { app.navigate(Screen.Calendar) },
                onOpenSettings = { app.navigate(Screen.Settings) },
                onOpenSpecialDay = { app.openPopup(AppPopup.SpecialDay) },
                showSpecialDay = settings.specialDayAlerts,
            )
            is Screen.Diary -> DiaryRoute(
                date = screen.date,
                onBack = { app.back() },
                onPassengersClick = { app.openPopup(AppPopup.Boarding) },
                onSendToPhone = { app.openPopup(AppPopup.SendPhone) },
                viewModel = diaryViewModel,
            )
            Screen.Calendar -> CalendarScreen(
                narration = narration,
                onBack = { app.back() },
                onOpenDiary = { d -> app.navigate(Screen.Diary(d?.date)) },
                onPlay = diaryViewModel::play,
            )
            Screen.Settings -> SettingsScreen(
                settings = settings,
                onBack = { app.back() },
                onCarName = app::setCarName,
                onSpecialDay = app::setSpecialDayAlerts,
                onRecordingOff = app::setRecordingOffToday,
                onAddMember = app::addMember,
                onUpdateMember = app::updateMember,
                onDeleteMember = app::deleteMember,
            )
            Screen.Driving -> {
                val t = trip
                DrivingScreen(
                    state = DrivingUiState(
                        fromPlace = t?.fromPlace ?: "우리 집",
                        startAt = t?.startAt ?: LocalDateTime.now(),
                        destination = t?.destination,
                        eta = t?.eta,
                        highlights = t?.highlights.orEmpty(),
                        resumed = t?.resumed ?: false,
                        recordingOff = settings.recordingOffToday,
                    ),
                    onBack = onLeaveApp,
                    onToggleRecording = app::toggleRecordingWhileDriving,
                )
            }
        }

        when (popup) {
            AppPopup.None -> Unit
            AppPopup.Boarding -> BoardingPopup(
                seats = boardingSeats,
                family = settings.family,
                today = today,
                onSetSeat = app::setBoardingSeat,
                onAddMember = app::addMember,
                onConfirm = app::confirmBoarding,
                onSkipToday = app::skipRecordingToday,
            )
            AppPopup.Arrival -> ArrivalPopup(
                destination = trip?.destination,
                childName = childName,
                secondsLeft = arrivalCountdown,
                onConfirm = { place -> app.finishArrival(place) },
                onLater = { app.finishArrival(place = null) },
                onPauseCountdown = app::pauseArrivalCountdown,
                onListen = app::listenPlaceName,
                onCancelListen = app::cancelListening,
                route = trip?.route.orEmpty().map { com.hyunsikyoo.chagokchagok.ui.designsystem.component.RouteMapPoint(it.latitude, it.longitude) },
            )
            AppPopup.SendPhone -> SendPhonePopup(
                diary = DemoDiaryRepository.diary(diaryState.date),
                carName = settings.carName,
                childName = childName,
                onClose = app::closePopup,
            )
            AppPopup.SpecialDay -> SpecialDayPopup(
                childName = childName,
                narration = narration,
                onPlay = { diaryViewModel.play(DemoDiaryRepository.yearAgo) },
                onOpenDiary = {
                    app.closePopup()
                    app.navigate(Screen.Diary(DemoDiaryRepository.yearAgo.date))
                },
                onClose = app::closePopup,
            )
        }

        // Toast: 앱 영역 아래 가운데, 3초
        toast?.let { message ->
            CarDiaryToast(message, Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp))
        }

        if (demoControls) {
            if (demoPanelOpen) {
                DemoControlPanel(
                    gear = gear,
                    noDestinationNext = noDestinationNext,
                    onDoor = app::onRearDoorOpened,
                    onDrive = { app.onGear(GearState.Drive) },
                    onPark = { app.onGear(GearState.Park) },
                    onEndTrip = app::endTripNow,
                    onToggleNoDestination = { app.demoNextTripWithoutDestination(!noDestinationNext) },
                    onClose = { demoPanelOpen = false },
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 24.dp),
                )
            }
            DemoHotspot(onLongPress = { demoPanelOpen = !demoPanelOpen }, modifier = Modifier.align(Alignment.BottomStart))
        }
    }
}
