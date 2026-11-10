package com.hyunsikyoo.chagokchagok.ui.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes

/**
 * Toggle (Figma 56:4220). 92×52, 모서리 100, 손잡이 40 원.
 * Off #FFFFFF / On #05E17A, 테두리 #000 2px, 손잡이 #FFFFFF + #000 2px.
 * 쓰는 곳: 설정 줄 오른쪽(List Row Trailing=Toggle). 움직임은 손잡이 이동만.
 */
@Composable
fun CarDiaryToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val thumbX by animateDpAsState(if (checked) 46.dp else 6.dp, label = "toggle-thumb")
    Box(
        modifier = modifier
            .size(width = 92.dp, height = 52.dp)
            .background(if (checked) CarDiaryColors.Green else CarDiaryColors.Card, CarDiaryShapes.Pill)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = thumbX)
                .size(40.dp)
                .background(CarDiaryColors.Card, CircleShape)
                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CircleShape),
        )
    }
}

/**
 * Progress Bar (Figma 56:4236). 560×20, 모서리 100. 트랙 #FFFFFF + 테두리 #000 2px, 채움 #FF7A05.
 * 쓰는 곳: 누적 거리 카드(다음 마일스톤 대비 비율), 마일스톤 화면. 숫자가 움직이는 효과 없음.
 */
@Composable
fun CarDiaryProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val fraction = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(560.dp)
            .height(20.dp)
            .clip(CarDiaryShapes.Pill)
            .background(CarDiaryColors.Card)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
            .semantics { if (contentDescription != null) this.contentDescription = contentDescription },
    ) {
        if (fraction > 0f) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(CarDiaryColors.Primary, CarDiaryShapes.Pill),
            )
        }
    }
}
