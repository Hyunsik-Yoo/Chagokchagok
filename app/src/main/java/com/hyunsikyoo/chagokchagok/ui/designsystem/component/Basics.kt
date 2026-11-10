package com.hyunsikyoo.chagokchagok.ui.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.LocalCarDiaryTextStyle

/** 글자. Material Text 대신 이것만 쓴다 */
@Composable
fun CarDiaryText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalCarDiaryTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(
            TextStyle(
                color = if (color != Color.Unspecified) color else style.color,
                textAlign = textAlign ?: style.textAlign,
            ),
        ),
        maxLines = maxLines,
        overflow = overflow,
    )
}

/**
 * Icon (Figma 53:3909). 기본 32×32, 선 #000. 다른 크기는 비율 유지.
 * 아이콘 목록은 [com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons].
 */
@Composable
fun CarDiaryIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    tint: Color = CarDiaryColors.Line,
) {
    Image(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}
