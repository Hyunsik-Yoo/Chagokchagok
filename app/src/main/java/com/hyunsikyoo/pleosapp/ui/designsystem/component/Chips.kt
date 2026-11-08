package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.hardShadow

/**
 * Tag (Figma 41:3753). 패딩 6/12, 모서리 100, 높이 36, 바탕 #000 10%, 글자 20 Regular #131417.
 * 쓰는 곳: Route Point 안 출발 / 도착 / 도착 예정.
 */
@Composable
fun CarDiaryTag(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(CarDiaryColors.TagBackground, CarDiaryShapes.Pill)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        CarDiaryText(label, style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextPleos, maxLines = 1)
    }
}

/** Piece Tag 변형 Style */
enum class PieceTagStyle { Default, More }

/**
 * Piece Tag (Figma 41:3764). 패딩 10/20, 간격 10, 모서리 100, 높이 58, 테두리 #000 2px.
 * Default #FFEC99 / More #FFFFFF, 글자 26 Regular #131417. 칩 8자 이내, 칩 사이 12.
 * 쓰는 곳: 오늘의 조각 칩 최대 4개 + '+N'(More).
 */
@Composable
fun CarDiaryPieceTag(
    label: String,
    modifier: Modifier = Modifier,
    style: PieceTagStyle = PieceTagStyle.Default,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .height(58.dp)
            .background(
                if (style == PieceTagStyle.Default) CarDiaryColors.PieceBackground else CarDiaryColors.Card,
                CarDiaryShapes.Pill,
            )
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        CarDiaryText(label, style = CarDiaryTypography.BodySmallNormal, color = CarDiaryColors.TextPleos, maxLines = 1)
    }
}

/** Status Pill 변형 Tone */
enum class StatusTone { Neutral, Accent }

/**
 * Status Pill (Figma 56:4234). 140×54, 패딩 8/18(왼) 8/20(오), 간격 10, 모서리 100, 테두리 #000 2px, 그림자 4/4.
 * Neutral #FFFFFF·점 #000 / Accent #DEFCE9·점 #00D696. 점 14, 라벨 26 Bold.
 * [blinking]: 점 깜빡임 1.5초 주기, 불투명도 100→40% 페이드(주행 중 '기록 중'). 주행 규제 최고 단계에서는 끈다.
 */
@Composable
fun CarDiaryStatusPill(
    label: String,
    modifier: Modifier = Modifier,
    tone: StatusTone = StatusTone.Accent,
    blinking: Boolean = false,
) {
    val dotAlpha = if (blinking) {
        val transition = rememberInfiniteTransition(label = "status-dot")
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Reverse),
            label = "status-dot-alpha",
        )
        alpha
    } else {
        1f
    }
    Row(
        modifier = modifier
            .padding(end = CarDiaryShadows.Soft, bottom = CarDiaryShadows.Soft)
            .height(54.dp)
            .hardShadow(CarDiaryShadows.Soft, CarDiaryShapes.Pill)
            .background(
                if (tone == StatusTone.Accent) CarDiaryColors.GreenBackground else CarDiaryColors.Card,
                CarDiaryShapes.Pill,
            )
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
            .padding(start = 18.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(14.dp)
                .alpha(dotAlpha)
                .background(if (tone == StatusTone.Accent) CarDiaryColors.Emerald else CarDiaryColors.Line, CircleShape),
        )
        CarDiaryText(label, style = CarDiaryTypography.BodySmallStrong, maxLines = 1)
    }
}

/**
 * Tab Chip (Figma 56:4226). 패딩 12/26, 모서리 100, 높이 62, 테두리 #000 2px, 글자 26.
 * Active #000 바탕 흰 글자 Bold / Default #FFFFFF 검정 글자 Regular.
 * 쓰는 곳: 월 탭(홈·달력), 사람 탭(마일스톤).
 */
@Composable
fun CarDiaryTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(62.dp)
            .background(if (selected) CarDiaryColors.Line else CarDiaryColors.Card, CarDiaryShapes.Pill)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        CarDiaryText(
            text = label,
            style = if (selected) CarDiaryTypography.BodySmallStrong else CarDiaryTypography.BodySmallNormal,
            color = if (selected) CarDiaryColors.Card else CarDiaryColors.Text,
            maxLines = 1,
        )
    }
}
