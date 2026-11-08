package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.dashedBorder
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons

/**
 * Route Point (Figma 42:3789). 세로, 간격 8. 장소 32 Bold #000, 시각 24 Regular #131417 + Tag.
 * 경로 줄에서 두 개를 화살표(선 40)로 잇는다 → [CarDiaryRoute].
 */
@Composable
fun CarDiaryRoutePoint(
    place: String,
    time: String,
    tag: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CarDiaryText(place, style = CarDiaryTypography.TitleSmallStrong, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CarDiaryText(time, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextPleos, maxLines = 1)
            CarDiaryTag(tag)
        }
    }
}

/** 출발 → 도착 경로 줄. [to]가 없으면(내비 목적지 없음) 출발지만 보여 준다 */
@Composable
fun CarDiaryRoute(
    from: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    to: (@Composable () -> Unit)? = null,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        from()
        if (to != null) {
            RouteArrow()
            to()
        }
    }
}

/** 경로 화살표: 선 40, 검정 50% */
@Composable
private fun RouteArrow() {
    Canvas(
        Modifier
            .size(width = 40.dp, height = 16.dp)
            .semantics { contentDescription = "에서" },
    ) {
        val stroke = 2.dp.toPx()
        val y = size.height / 2
        val color = CarDiaryColors.TextSub
        drawLine(color, Offset(0f, y), Offset(size.width, y), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width - 8.dp.toPx(), y - 6.dp.toPx()), Offset(size.width, y), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width - 8.dp.toPx(), y + 6.dp.toPx()), Offset(size.width, y), stroke, StrokeCap.Round)
    }
}

/** Avatar 변형 (Figma Property 1) */
enum class AvatarSize(val size: Dp, val icon: Dp) { Default(68.dp, 32.dp), Small(40.dp, 20.dp) }

/**
 * Avatar (Figma 45:8289). Default 68 / Small 40 원. 사진 또는 자리 표시 아이콘.
 * 자리 표시는 Figma 그림 그대로 Amber 원 + 테두리 2px + user 아이콘, Default는 그림자 4/4.
 */
@Composable
fun CarDiaryAvatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Default,
    photo: Painter? = null,
    contentDescription: String? = null,
) {
    val shadow = if (size == AvatarSize.Default) Modifier.hardShadow(CarDiaryShadows.Soft, CircleShape) else Modifier
    Box(
        modifier = modifier
            .size(size.size)
            .then(shadow)
            .background(CarDiaryColors.AvatarBackground, CircleShape)
            .clip(CircleShape)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (photo != null) {
            Image(photo, contentDescription, contentScale = ContentScale.Crop, modifier = Modifier.size(size.size))
        } else {
            CarDiaryIcon(CarDiaryIcons.User, contentDescription = contentDescription, size = size.icon)
        }
    }
}

/** 탑승자 겹침: Avatar를 간격 60으로 겹쳐 놓는다 (함께 탄 사람) */
@Composable
fun CarDiaryAvatarStack(
    count: Int,
    modifier: Modifier = Modifier,
    step: Dp = 60.dp,
) {
    if (count <= 0) return
    // offset은 레이아웃 크기에 안 잡히므로 전체 폭을 직접 잡는다
    val full = AvatarSize.Default.size + CarDiaryShadows.Soft
    Box(
        modifier = modifier
            .width(step * (count - 1) + full)
            .height(full),
    ) {
        repeat(count) { index ->
            CarDiaryAvatar(Modifier.offset(x = step * index))
        }
    }
}

/**
 * Section Header (Figma 41:3769). 가로, 양 끝 정렬, 높이 38.
 * 제목 30 Bold #000, 개수 30 Bold #5294FF, 오른쪽 전체보기 Text Button.
 */
@Composable
fun CarDiarySectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    moreLabel: String = "전체보기",
    onMore: (() -> Unit)? = null,
    /** Leading=Icon(11/8): 앞에 아이콘 24 + 제목, 개수 없음 */
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(38.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (leadingIcon != null) CarDiaryIcon(leadingIcon, contentDescription = null, size = 24.dp)
            CarDiaryText(title, style = CarDiaryTypography.BodyLargeStrong, maxLines = 1)
            if (count != null && leadingIcon == null) {
                CarDiaryText(count.toString(), style = CarDiaryTypography.BodyLargeStrong, color = CarDiaryColors.Main)
            }
        }
        if (onMore != null) CarDiaryTextButton(moreLabel, onClick = onMore)
    }
}

/**
 * Highlight Row (Figma 41:3794). 가로, 간격 44, 높이 34.
 * 시각 26 Regular #000 50%, 오른쪽 묶음은 아이콘 32 + 제목 26 Bold(간격 8). 제목 12자 이내.
 * 아이콘은 Fluent 3D 이모지 PNG(120px)로 바꿀 예정 — 지금은 [icon] 이미지가 없으면 [emoji] 글자로 그린다.
 */
@Composable
fun CarDiaryHighlightRow(
    time: String,
    text: String,
    emoji: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    Row(
        modifier = modifier.height(34.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(44.dp),
    ) {
        CarDiaryText(time, style = CarDiaryTypography.BodySmallNormal, color = CarDiaryColors.TextSub, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                if (icon != null) {
                    Image(icon, contentDescription = null, modifier = Modifier.size(32.dp))
                } else {
                    CarDiaryText(emoji, style = CarDiaryTypography.BodySmallNormal.copy(fontSize = 26.sp, lineHeight = 32.sp))
                }
            }
            CarDiaryText(text, style = CarDiaryTypography.BodySmallStrong, maxLines = 1)
        }
    }
}

/** 내용 자리 표시(점선 칸). 시안·카탈로그에서 아직 안 채운 영역에 쓴다 */
@Composable
fun CarDiaryPlaceholder(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(CarDiaryColors.Surface, CarDiaryShapes.ListRow)
            .dashedBorder(CarDiaryDimens.Stroke, CarDiaryColors.LineSoft, CarDiaryShapes.ListRow)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        CarDiaryText(label, style = CarDiaryTypography.BodySmallNormal, color = CarDiaryColors.TextSub)
    }
}
