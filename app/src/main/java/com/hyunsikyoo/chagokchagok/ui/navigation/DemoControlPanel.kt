package com.hyunsikyoo.chagokchagok.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.data.vehicle.GearState
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonSize
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.hardShadow

/**
 * 시연 조작 (디버그 빌드만). PT에서 adb 없이 차 신호를 흉내 낸다.
 * 앱 영역 왼쪽 아래 구석(보이지 않는 칸)을 길게 누르면 열리고 닫힌다.
 * 버튼은 adb 시연 신호(door, drive, park, end, nodest)와 같은 길로 들어간다.
 */
@Composable
fun DemoHotspot(onLongPress: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(HOTSPOT_SIZE)
            .pointerInput(Unit) { detectTapGestures(onLongPress = { onLongPress() }) },
    )
}

@Composable
fun DemoControlPanel(
    gear: GearState,
    noDestinationNext: Boolean,
    onDoor: () -> Unit,
    onDrive: () -> Unit,
    onPark: () -> Unit,
    onEndTrip: () -> Unit,
    onToggleNoDestination: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .width(PANEL_WIDTH)
            .hardShadow(CarDiaryShadows.Card, CarDiaryShapes.Card)
            .background(CarDiaryColors.Card, CarDiaryShapes.Card)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            CarDiaryText("시연 조작", style = CarDiaryTypography.BodyMediumStrong)
            CarDiaryText("지금 기어 ${gear.label()}", style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
        }
        val full = Modifier.fillMaxWidth()
        CarDiaryButton("뒷문 열림", onDoor, full, style = ButtonStyle.Neutral, size = ButtonSize.Small, enabled = gear != GearState.Drive)
        CarDiaryButton("주행 D", onDrive, full, style = ButtonStyle.Primary, size = ButtonSize.Small, enabled = gear != GearState.Drive)
        CarDiaryButton("주차 P", onPark, full, style = ButtonStyle.Secondary, size = ButtonSize.Small, enabled = gear == GearState.Drive)
        CarDiaryButton("트립 끝내기", onEndTrip, full, style = ButtonStyle.Neutral, size = ButtonSize.Small)
        CarDiaryButton(
            if (noDestinationNext) "목적지 없음 ✓" else "목적지 없이",
            onToggleNoDestination,
            full,
            style = if (noDestinationNext) ButtonStyle.Secondary else ButtonStyle.Neutral,
            size = ButtonSize.Small,
        )
        CarDiaryButton("닫기", onClose, full, style = ButtonStyle.Neutral, size = ButtonSize.Small)
        CarDiaryText("왼쪽 아래 구석 길게 누르면 열고 닫혀요. 디버그 빌드만", style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub)
    }
}

private fun GearState.label() = when (this) {
    GearState.Drive -> "D"
    GearState.Park, GearState.Unknown -> "P"
    GearState.Other -> "R/N"
}

private val HOTSPOT_SIZE = 96.dp
/** 팝업(폭 1200, 가운데) 왼쪽 여백 250 안에 들어가는 폭 */
private val PANEL_WIDTH = 220.dp
