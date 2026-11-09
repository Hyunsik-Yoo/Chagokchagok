package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons

/** Header 변형 Nav. Mix = 뒤로 + 제목 + 오른쪽 칸들(홈: 기록 시작 · 설정) → [CarDiaryHeader]의 actions */
enum class HeaderNav { Both, BackOnly, None, Mix }

/** Header 오른쪽 이전·다음 칸 하나 */
data class HeaderNavItem(
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    /** 라벨 앞 아이콘 (Mix의 설정 칸) */
    val icon: ImageVector? = null,
)

/**
 * Header (Figma 50:3924). 1700×110, 배경 없음(앱 창 바탕 비침), 아래 검정 2px 선.
 * - 왼쪽 Icon Button(arrow-left) + 제목 30 Bold #131417.
 * - Both: 오른쪽에 '‹ 이전 일기' '다음 일기 ›' 30 Bold, 세로선으로 칸 구분.
 * - 쓰는 곳: 일기 = Both, 주행 중·달력·설정 = BackOnly, 홈 = Mix(기록 시작 · 설정, 뒤로 없음).
 * - [showBack] false면 Nav와 상관없이 왼쪽 뒤로 가기를 뺀다 (첫 화면 홈).
 */
@Composable
fun CarDiaryHeader(
    title: String,
    modifier: Modifier = Modifier,
    nav: HeaderNav = HeaderNav.BackOnly,
    onBack: () -> Unit = {},
    showBack: Boolean = true,
    prev: HeaderNavItem? = null,
    next: HeaderNavItem? = null,
    /** Mix: 오른쪽 칸들 (세로선으로 구분) */
    actions: List<HeaderNavItem> = emptyList(),
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(CarDiaryDimens.HeaderHeight)
            .drawBehind {
                val stroke = CarDiaryDimens.Stroke.toPx()
                drawLine(
                    color = CarDiaryColors.Line,
                    start = Offset(0f, size.height - stroke / 2),
                    end = Offset(size.width, size.height - stroke / 2),
                    strokeWidth = stroke,
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(CarDiaryDimens.PagePadding))
        if (nav != HeaderNav.None && showBack) {
            CarDiaryIconButton(CarDiaryIcons.ArrowLeft, contentDescription = "뒤로 가기", onClick = onBack)
            Spacer(Modifier.width(40.dp))
        }
        CarDiaryText(
            text = title,
            style = CarDiaryTypography.BodyLargeStrong,
            color = CarDiaryColors.TextPleos,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        if (nav == HeaderNav.Both) {
            prev?.let { HeaderNavCell(it, leading = CarDiaryIcons.ChevronLeft) }
            next?.let { HeaderNavCell(it, trailing = CarDiaryIcons.ChevronRight) }
        }
        if (nav == HeaderNav.Mix) {
            actions.forEach { HeaderNavCell(it, leading = it.icon) }
        }
        if (trailing != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                content = trailing,
            )
            Spacer(Modifier.width(CarDiaryDimens.PagePadding))
        }
    }
}

@Composable
private fun HeaderNavCell(
    item: HeaderNavItem,
    leading: ImageVector? = null,
    trailing: ImageVector? = null,
) {
    val color = if (item.enabled) CarDiaryColors.Text else CarDiaryColors.TextDisabled
    Row(
        modifier = Modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(CarDiaryDimens.Stroke)
                .fillMaxHeight()
                .background(CarDiaryColors.Line),
        )
        Row(
            modifier = Modifier
                .width(254.dp)
                .fillMaxHeight()
                .clickable(enabled = item.enabled, role = Role.Button, onClick = item.onClick)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            leading?.let { CarDiaryIcon(it, contentDescription = null, size = 28.dp, tint = color) }
            CarDiaryText(item.label, style = CarDiaryTypography.BodyLargeStrong, color = color, maxLines = 1)
            trailing?.let { CarDiaryIcon(it, contentDescription = null, size = 28.dp, tint = color) }
        }
    }
}
