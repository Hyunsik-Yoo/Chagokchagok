package com.hyunsikyoo.chagokchagok.ui.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.R
import com.hyunsikyoo.chagokchagok.domain.model.FluentEmoji
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors

/** Fluent 3D 이모지 PNG(120px, res/drawable-nodpi) */
@DrawableRes
fun FluentEmoji.drawableRes(): Int = when (this) {
    FluentEmoji.Droplet -> R.drawable.fluent_droplet
    FluentEmoji.Motorway -> R.drawable.fluent_motorway
    FluentEmoji.HouseWithGarden -> R.drawable.fluent_house_with_garden
    FluentEmoji.Sparkles -> R.drawable.fluent_sparkles
    FluentEmoji.SunBehindCloud -> R.drawable.fluent_sun_behind_cloud
    FluentEmoji.Balloon -> R.drawable.fluent_balloon
    FluentEmoji.HotBeverage -> R.drawable.fluent_hot_beverage
    FluentEmoji.Mountain -> R.drawable.fluent_mountain
    FluentEmoji.LeafFlutteringInWind -> R.drawable.fluent_leaf_fluttering_in_wind
    FluentEmoji.ChequeredFlag -> R.drawable.fluent_chequered_flag
    FluentEmoji.Snowflake -> R.drawable.fluent_snowflake
    FluentEmoji.Star -> R.drawable.fluent_star
    FluentEmoji.CherryBlossom -> R.drawable.fluent_cherry_blossom
    FluentEmoji.Fog -> R.drawable.fluent_fog
    FluentEmoji.FullMoon -> R.drawable.fluent_full_moon
    FluentEmoji.Sun -> R.drawable.fluent_sun
    FluentEmoji.SleepingFace -> R.drawable.fluent_sleeping_face
}

@Composable
fun FluentEmoji.painter(): Painter = painterResource(drawableRes())

/**
 * 일기 종이 위 스티커: Fluent 3D 이모지 + 검정 하드 섀도(같은 모양을 오른쪽·아래로 밀어 검정으로).
 * 기본 120 (핸드오프 "이모지 PNG 120px").
 */
@Composable
fun FluentSticker(
    emoji: FluentEmoji,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    shadowOffset: Dp = 6.dp,
) {
    val painter = emoji.painter()
    Box(modifier.size(size + shadowOffset)) {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(CarDiaryColors.Line),
            modifier = Modifier.offset(shadowOffset, shadowOffset).size(size),
        )
        Image(painter = painter, contentDescription = emoji.char, modifier = Modifier.size(size))
    }
}
