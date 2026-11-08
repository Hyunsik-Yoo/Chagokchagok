package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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

/** Card 변형 Shadow */
enum class CardShadow { None, Hard }

/**
 * Card (Figma 56:4273). 세로, 패딩 34, 간격 24, 모서리 24, #FFFFFF, 테두리 #000 2px.
 * Hard는 그림자 6/6 — 화면당 가장 중요한 카드 1장만(홈 최근 일기, 주행 중 명장면).
 * 폭은 부르는 쪽이 정한다(기본 시안 629).
 */
@Composable
fun CarDiaryCard(
    modifier: Modifier = Modifier,
    shadow: CardShadow = CardShadow.None,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shadowModifier = if (shadow == CardShadow.Hard) {
        Modifier.hardShadow(CarDiaryShadows.Card, CarDiaryShapes.Card)
    } else {
        Modifier
    }
    Column(
        modifier = modifier
            .then(shadowModifier)
            .background(CarDiaryColors.Card, CarDiaryShapes.Card)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
            .padding(CarDiaryDimens.CardPadding),
        verticalArrangement = Arrangement.spacedBy(CarDiaryDimens.CardGap),
        content = content,
    )
}

/**
 * Banner (Figma 56:4239). 가로, 패딩 18/26, 간격 16, 모서리 24, 560×102, #FDF7C4, 테두리 #000 2px, 그림자 4/4.
 * 캡션 20 Regular #000 50%, 제목 28 Bold, 왼쪽 아이콘 40, 오른쪽 chevron 28.
 * 쓰는 곳: 홈 '1년 전 오늘', 달력 연말 결산 예고. 누르면 팝업.
 */
@Composable
fun CarDiaryBanner(
    caption: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = CarDiaryIcons.Calendar,
) {
    Row(
        modifier = modifier
            .padding(end = CarDiaryShadows.Soft, bottom = CarDiaryShadows.Soft)
            .height(102.dp)
            .hardShadow(CarDiaryShadows.Soft, CarDiaryShapes.Card)
            .background(CarDiaryColors.YellowBackground, CarDiaryShapes.Card)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CarDiaryIcon(icon, contentDescription = null, size = 40.dp)
        Column(Modifier.weight(1f)) {
            CarDiaryText(caption, style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub, maxLines = 1)
            CarDiaryText(title, style = CarDiaryTypography.BodyMediumStrong, maxLines = 1)
        }
        CarDiaryIcon(CarDiaryIcons.ChevronRight, contentDescription = null, size = 28.dp)
    }
}

/** List Row 변형 Trailing */
sealed interface ListRowTrailing {
    data object Chevron : ListRowTrailing
    data class Toggle(val checked: Boolean, val onCheckedChange: (Boolean) -> Unit) : ListRowTrailing
    /** Checkbox(11/8): 줄의 selected를 따라 Off/On */
    data object Checkbox : ListRowTrailing
    /** 아이콘 하나 (새 가족 추가 +, 이름 붙이기 마이크) */
    data class Icon(val icon: ImageVector) : ListRowTrailing
}

/**
 * List Row (Figma 57:4169). 592×108, 가로, 패딩 18/24, 간격 16, 모서리 20.
 * Default #FFFFFF + 테두리 #000 20% / Selected #DDEFFD + 테두리 #000. Trailing: Chevron / Toggle / Checkbox(11/8).
 * 제목 26 Bold, 보조 24 Regular 50%, 오른쪽 chevron 32 또는 Toggle.
 * 쓰는 곳: 설정 목록, 가족 목록.
 */
@Composable
fun CarDiaryListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    selected: Boolean = false,
    trailing: ListRowTrailing = ListRowTrailing.Chevron,
    /** 줄 바탕을 바꿀 때 (도착 팝업의 이름 붙이기 줄 = 노랑) */
    background: androidx.compose.ui.graphics.Color? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .height(108.dp)
            .background(background ?: if (selected) CarDiaryColors.SkyBackground else CarDiaryColors.Card, CarDiaryShapes.ListRow)
            .border(
                CarDiaryDimens.Stroke,
                if (selected) CarDiaryColors.Line else CarDiaryColors.LineSoft,
                CarDiaryShapes.ListRow,
            )
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            CarDiaryText(title, style = CarDiaryTypography.BodySmallStrong, maxLines = 1)
            if (subtitle != null) {
                CarDiaryText(subtitle, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub, maxLines = 1)
            }
        }
        when (trailing) {
            ListRowTrailing.Chevron -> CarDiaryIcon(CarDiaryIcons.ChevronRight, contentDescription = null, size = 32.dp)
            is ListRowTrailing.Toggle -> CarDiaryToggle(trailing.checked, trailing.onCheckedChange)
            ListRowTrailing.Checkbox -> CarDiaryCheckbox(selected)
            is ListRowTrailing.Icon -> CarDiaryIcon(trailing.icon, contentDescription = null, size = 32.dp)
        }
    }
}
