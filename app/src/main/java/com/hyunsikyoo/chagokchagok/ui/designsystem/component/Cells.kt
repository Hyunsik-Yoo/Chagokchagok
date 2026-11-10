package com.hyunsikyoo.chagokchagok.ui.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.dashedBorder
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons

/**
 * Seat Card (Figma 58:4391). 524×192, 세로, 패딩 22/28, 간격 10, 모서리 28.
 * Filled #DDEFFD + 테두리 #000 / Empty #FFFFFF + 점선 #000 20%.
 * 자리 24 Regular 50%, 이름 36 Bold(Empty는 #000 35% + '+ 추가' 24). Show age(11/8): 아이는 이름 옆 나이 Tag.
 * 쓰는 곳: 탑승 팝업 4칸, 설정 가족과 자리.
 */
@Composable
fun CarDiarySeatCard(
    seat: String,
    name: String,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ageLabel: String? = null,
) {
    val shapeModifier = if (filled) {
        Modifier
            .background(CarDiaryColors.SkyBackground, CarDiaryShapes.Seat)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Seat)
    } else {
        Modifier
            .background(CarDiaryColors.Card, CarDiaryShapes.Seat)
            .dashedBorder(CarDiaryDimens.Stroke, CarDiaryColors.LineSoft, CarDiaryShapes.Seat)
    }
    Column(
        modifier = modifier
            .size(width = 524.dp, height = 192.dp)
            .then(shapeModifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CarDiaryText(seat, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub, maxLines = 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (filled) {
                CarDiaryAvatar()
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarDiaryText(name, style = CarDiaryTypography.TitleMediumStrong, maxLines = 1)
                    if (ageLabel != null) CarDiaryTag(ageLabel)
                }
                CarDiaryIcon(CarDiaryIcons.Check, contentDescription = "선택됨", size = 40.dp, tint = CarDiaryColors.AccentLink)
            } else {
                Box(
                    Modifier
                        .size(76.dp)
                        .dashedBorder(CarDiaryDimens.Stroke, CarDiaryColors.LineSoft, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    CarDiaryIcon(CarDiaryIcons.Plus, contentDescription = null, size = 32.dp, tint = CarDiaryColors.TextDisabled)
                }
                CarDiaryText(
                    name,
                    style = CarDiaryTypography.TitleMediumStrong,
                    color = CarDiaryColors.TextDisabled,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                CarDiaryText("+ 추가", style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextDisabled)
            }
        }
    }
}

/** Calendar Day 변형 State (11/8: Short 삭제 — 긴 일기·짧은 일기 모두 Diary) */
enum class CalendarDayState { Empty, Diary, Selected }

/**
 * Calendar Day (Figma 58:4404). 124×148, 모서리 18.
 * - Empty(안 탄 날·아직 안 온 날): #000 5% 바탕, 날짜 Regular.
 * - Diary(일기 쓴 날): #FFFFFF + #000 20% 테두리, 날짜 Bold, 오른쪽 아래 그날 첫 명장면 이모지.
 * - Selected(고른 날): 시안 그림 기준 노랑(#FDF7C4) + #000 테두리.
 * - Today(11/8): 날짜를 검정 원 안 흰 글자로.
 */
@Composable
fun CarDiaryCalendarDay(
    day: Int,
    state: CalendarDayState,
    modifier: Modifier = Modifier,
    today: Boolean = false,
    emoji: Painter? = null,
    onClick: (() -> Unit)? = null,
) {
    val box = when (state) {
        CalendarDayState.Empty -> Modifier.background(CarDiaryColors.Surface, CarDiaryShapes.CalendarDay)
        CalendarDayState.Diary -> Modifier
            .background(CarDiaryColors.Card, CarDiaryShapes.CalendarDay)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.LineSoft, CarDiaryShapes.CalendarDay)
        CalendarDayState.Selected -> Modifier
            .background(CarDiaryColors.YellowBackground, CarDiaryShapes.CalendarDay)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.CalendarDay)
    }
    Box(
        modifier = modifier
            .size(width = 124.dp, height = 148.dp)
            .then(box)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(12.dp),
    ) {
        if (today) {
            Box(
                Modifier.align(Alignment.TopStart).size(34.dp).background(CarDiaryColors.Line, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                CarDiaryText(day.toString(), style = CarDiaryTypography.LabelSmall.copy(fontWeight = CarDiaryTypography.BodySmallStrong.fontWeight), color = CarDiaryColors.Card)
            }
        } else {
            CarDiaryText(
                day.toString(),
                style = if (state == CalendarDayState.Empty) CarDiaryTypography.BodySmallNormal else CarDiaryTypography.BodySmallStrong,
                color = CarDiaryColors.Text,
                modifier = Modifier.align(Alignment.TopStart),
            )
        }
        if (state != CalendarDayState.Empty && emoji != null) {
            Image(emoji, contentDescription = null, modifier = Modifier.align(Alignment.BottomEnd).size(52.dp))
        }
    }
}

/**
 * Milestone Card (Figma 58:4423). 미사용 — 마일스톤 화면(W5)이 범위 제외(11/8). 나중을 위해 남겨 둔다.
 * 156×290, 세로, 패딩 20/14, 간격 10, 모서리 24.
 */
@Composable
fun CarDiaryMilestoneCard(
    distance: String,
    caption: String,
    done: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector = if (done) CarDiaryIcons.Star else CarDiaryIcons.Lock,
    statusLabel: String = if (done) "✓ 달성" else "잠김",
    onClick: (() -> Unit)? = null,
) {
    val card = if (done) {
        Modifier
            .background(CarDiaryColors.Card, CarDiaryShapes.Card)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
    } else {
        Modifier
            .background(CarDiaryColors.Surface2, CarDiaryShapes.Card)
            .dashedBorder(CarDiaryDimens.Stroke, CarDiaryColors.LineSoft, CarDiaryShapes.Card)
    }
    val badge = if (done) {
        Modifier
            .background(CarDiaryColors.YellowBackground, CircleShape)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CircleShape)
    } else {
        Modifier
            .background(CarDiaryColors.LineLight, CircleShape)
            .dashedBorder(CarDiaryDimens.Stroke, CarDiaryColors.LineSoft, CircleShape)
    }
    Column(
        modifier = modifier
            .size(width = 156.dp, height = 290.dp)
            .then(card)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(84.dp).then(badge), contentAlignment = Alignment.Center) {
            CarDiaryIcon(icon, contentDescription = null, size = 40.dp, tint = if (done) CarDiaryColors.Line else CarDiaryColors.TextDisabled)
        }
        CarDiaryText(distance, style = CarDiaryTypography.BodySmallStrong, color = if (done) CarDiaryColors.Text else CarDiaryColors.TextDisabled, maxLines = 1)
        CarDiaryText(
            caption,
            style = CarDiaryTypography.LabelSmall,
            color = if (done) CarDiaryColors.TextCaption else CarDiaryColors.TextDisabled,
            textAlign = TextAlign.Center,
            maxLines = 3,
        )
        Spacer(Modifier.weight(1f))
        CarDiaryText(statusLabel, style = CarDiaryTypography.LabelMedium, color = if (done) CarDiaryColors.AccentLink else CarDiaryColors.TextDisabled)
    }
}
