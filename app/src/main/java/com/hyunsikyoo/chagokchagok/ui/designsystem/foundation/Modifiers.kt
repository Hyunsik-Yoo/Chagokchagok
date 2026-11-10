package com.hyunsikyoo.chagokchagok.ui.designsystem.foundation

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 뉴브루탈리즘 하드 섀도: 같은 모양을 오른쪽·아래로 [offset]만큼 밀어 검정으로 칠한다(흐림 0).
 * 배경보다 먼저 걸어야 한다: `Modifier.hardShadow(...).background(...)`.
 * 그림자는 요소 밖으로 그려지므로 레이아웃 자리는 부모가 남겨 둔다.
 */
fun Modifier.hardShadow(
    offset: Dp,
    shape: Shape = RectangleShape,
    color: Color = CarDiaryColors.Line,
): Modifier = drawBehind {
    val o = offset.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(o, o) { drawOutline(outline, color) }
}

/** 점선 테두리 (Seat Card Empty, Milestone Locked, 내용 자리) */
fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    dash: Dp = 10.dp,
    gap: Dp = 8.dp,
): Modifier = drawWithContent {
    drawContent()
    val stroke = width.toPx()
    val inset = stroke / 2
    val outline = shape.createOutline(
        Size(size.width - stroke, size.height - stroke),
        layoutDirection,
        this,
    )
    translate(inset, inset) {
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(
                width = stroke,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.toPx(), gap.toPx())),
            ),
        )
    }
}
