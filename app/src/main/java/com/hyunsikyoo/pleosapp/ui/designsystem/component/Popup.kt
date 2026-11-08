package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons

/** 팝업 아래 버튼 하나 */
data class PopupAction(
    val label: String,
    val onClick: () -> Unit,
    val style: ButtonStyle,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
)

/**
 * Popup (Figma 57:4119). 폭 1200, 세로, 패딩 48/56, 간격 24, 모서리 48, #FFFFFF, 테두리 #000 2px, 그림자 8/8.
 * - 제목 48 Bold. Show back(11/8): 제목 앞 뒤로 가기 Icon Button — 팝업 안에서 단계가 넘어갈 때(탑승 자리 고르기·새 가족 추가, 도착 이름 붙이기).
 * - 설명 26 Regular 50%, 내용, 오른쪽 정렬 버튼(보조 Neutral → 주 Primary), 버튼 아래 오른쪽에 안내 한 줄.
 * - 높이는 내용만큼(디자인 완료 시안 기준). 주행 중엔 절대 띄우지 않는다. 뒤 화면 덮기는 [CarDiaryPopupScrim].
 */
@Composable
fun CarDiaryPopup(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    actions: List<PopupAction> = emptyList(),
    /** 버튼 아래 안내 한 줄 (자동 닫힘·자동 저장 안내) */
    footnote: String? = null,
    /** 제목 위 작은 배지 (특별한 날 종류) */
    badge: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .width(CarDiaryDimens.PopupWidth)
            .heightIn(max = 1080.dp)
            .hardShadow(CarDiaryShadows.Popup, CarDiaryShapes.Popup)
            .background(CarDiaryColors.Card, CarDiaryShapes.Popup)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Popup)
            // 팝업 안을 눌러도 뒤 덮개로 클릭이 새지 않게
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {})
            .padding(horizontal = 56.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            badge?.invoke()
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (onBack != null) CarDiaryIconButton(CarDiaryIcons.ArrowLeft, contentDescription = "뒤로", onClick = onBack)
                CarDiaryText(title, style = CarDiaryTypography.HeadlineSmallStrong, maxLines = 1, modifier = Modifier.weight(1f))
                if (onClose != null) CarDiaryIconButton(CarDiaryIcons.X, contentDescription = "닫기", onClick = onClose)
            }
            if (subtitle != null) {
                CarDiaryText(subtitle, style = CarDiaryTypography.BodySmallNormal, color = CarDiaryColors.TextSub)
            }
        }
        Column(Modifier.fillMaxWidth(), content = content)
        if (actions.isNotEmpty() || footnote != null) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (actions.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ButtonGap)) {
                        actions.forEach {
                            CarDiaryButton(it.label, onClick = it.onClick, style = it.style, icon = it.icon, enabled = it.enabled)
                        }
                    }
                }
                if (footnote != null) {
                    CarDiaryText(footnote, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
                }
            }
        }
    }
}

/**
 * 팝업 뒤 화면 덮기: 앱 창 전체를 검정 45%로 덮고 가운데에 팝업을 놓는다.
 * [onDismiss]가 있으면 덮개를 눌렀을 때 닫는다.
 */
@Composable
fun CarDiaryPopupScrim(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CarDiaryColors.Scrim)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss?.invoke() }
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
