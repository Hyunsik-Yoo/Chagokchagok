package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons

/** Button 변형 Style */
enum class ButtonStyle { Primary, Secondary, Neutral }

/** Button 변형 Size (11/8): Large 겉 86 / Small 겉 72 (정차 중 보조 버튼) */
enum class ButtonSize { Large, Small }

/**
 * Button (Figma 40:3752). Style: Primary / Secondary / Neutral × State: Default / Pressed / Disabled.
 * - 겉 높이 86 안에 Body 82. Default는 겉 패딩 오른쪽·아래 4(그림자 자리), Pressed는 몸통이 4px 내려앉고 그림자 사라짐.
 * - 겉 크기는 세 상태 동일.
 * - 쓰는 곳: 들려주기(Primary+play), 폰으로 보내기(Secondary+send), 기록 끄기(Secondary, 아이콘 없음), 팝업 버튼(Neutral → Primary).
 */
@Composable
fun CarDiaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Large,
    /** 카탈로그에서 Pressed 상태를 고정해 보여 줄 때만 쓴다 */
    forcePressed: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressedNow by interaction.collectIsPressedAsState()
    val pressed = enabled && (pressedNow || forcePressed)

    val body = when {
        !enabled -> CarDiaryColors.DisabledFill
        style == ButtonStyle.Primary -> CarDiaryColors.Primary
        style == ButtonStyle.Secondary -> CarDiaryColors.Main
        else -> CarDiaryColors.Card
    }
    val stroke = if (enabled) CarDiaryColors.Line else CarDiaryColors.DisabledStroke
    val content = if (enabled) CarDiaryColors.Text else CarDiaryColors.TextDisabled
    val shadow = CarDiaryShadows.Soft

    Box(
        modifier = modifier
            .height(if (size == ButtonSize.Large) CarDiaryDimens.ButtonHeight else 72.dp)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                if (pressed) PaddingValues(start = shadow, top = shadow) else PaddingValues(end = shadow, bottom = shadow),
            ),
    ) {
        Row(
            modifier = Modifier
                .height(if (size == ButtonSize.Large) CarDiaryDimens.ButtonBodyHeight else 68.dp)
                .then(if (enabled && !pressed) Modifier.hardShadow(shadow, CarDiaryShapes.Pill) else Modifier)
                .background(body, CarDiaryShapes.Pill)
                .border(CarDiaryDimens.Stroke, stroke, CarDiaryShapes.Pill)
                .padding(horizontal = if (size == ButtonSize.Large) 40.dp else 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            if (icon != null) CarDiaryIcon(icon, contentDescription = null, size = if (size == ButtonSize.Large) 32.dp else 28.dp, tint = content)
            CarDiaryText(
                label,
                style = if (size == ButtonSize.Large) CarDiaryTypography.BodyLargeStrong else CarDiaryTypography.BodySmallStrong,
                color = content,
                maxLines = 1,
            )
        }
    }
}

/**
 * Icon Button (Figma 40:3726). 60×60, 패딩 8, 모서리 100, 배경 없음, 테두리 #000 2px, 아이콘 40.
 * 쓰는 곳: 헤더 뒤로 가기, 홈 설정, 팝업 닫기.
 */
@Composable
fun CarDiaryIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .border(CarDiaryDimens.Stroke, if (enabled) CarDiaryColors.Line else CarDiaryColors.DisabledStroke, CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClickLabel = contentDescription, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        CarDiaryIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = 40.dp,
            tint = if (enabled) CarDiaryColors.Line else CarDiaryColors.TextDisabled,
        )
    }
}

/**
 * Text Button (Figma 54:6458). 가로, 간격 8, 높이 34. 라벨 26 Regular #000 50%, 아이콘 24(chevron-right).
 * 쓰는 곳: Section Header의 전체보기, 달력으로 보기, 마일스톤 보기.
 */
@Composable
fun CarDiaryTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = CarDiaryIcons.ChevronRight,
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CarDiaryText(label, style = CarDiaryTypography.BodySmallNormal, color = CarDiaryColors.TextSub, maxLines = 1)
        if (icon != null) CarDiaryIcon(icon, contentDescription = null, size = 24.dp, tint = CarDiaryColors.TextSub)
    }
}
