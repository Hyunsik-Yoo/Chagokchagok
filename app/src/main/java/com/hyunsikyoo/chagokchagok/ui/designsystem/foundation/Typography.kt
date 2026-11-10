package com.hyunsikyoo.chagokchagok.ui.designsystem.foundation

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.hyunsikyoo.chagokchagok.R

/** Asta Sans (Pleos 기본 글꼴, OFL). 가변 글꼴 한 파일을 굵기별로 나눠 쓴다. */
@OptIn(ExperimentalTextApi::class)
val AstaSans = FontFamily(
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.Bold, FontWeight.ExtraBold).map { weight ->
        Font(
            resId = R.font.asta_sans,
            weight = weight,
            variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
        )
    },
)

private fun style(size: Int, weight: FontWeight, lineHeight: Int): TextStyle = style(size.sp, weight, lineHeight.sp)

private fun style(size: TextUnit, weight: FontWeight, lineHeight: TextUnit): TextStyle = TextStyle(
    fontFamily = AstaSans,
    fontSize = size,
    fontWeight = weight,
    lineHeight = lineHeight,
    color = CarDiaryColors.Text,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
)

/**
 * 글자 스타일 — Pleos 텍스트 스타일 (Asta Sans). 크기·줄 간격은 2560×1440 화면 px 그대로(160dpi라 1dp = 1px).
 * Extra_Strong = ExtraBold(800), Strong = Bold(700), Normal = Regular(400).
 * 글자 최소 20 (Label/Small).
 */
object CarDiaryTypography {
    // Headline
    val HeadlineLargeExtraStrong = style(60, FontWeight.ExtraBold, 80)
    val HeadlineLargeStrong = style(60, FontWeight.Bold, 80)
    /** 누적 거리 3,214km */
    val HeadlineMediumExtraStrong = style(56, FontWeight.ExtraBold, 72)
    val HeadlineMediumStrong = style(56, FontWeight.Bold, 72)
    /** 일기 제목 */
    val HeadlineSmallExtraStrong = style(48, FontWeight.ExtraBold, 64)
    /** 팝업 제목 */
    val HeadlineSmallStrong = style(48, FontWeight.Bold, 64)

    // Title
    val TitleLargeExtraStrong = style(40, FontWeight.ExtraBold, 52)
    val TitleLargeStrong = style(40, FontWeight.Bold, 52)
    val TitleLargeNormal = style(40, FontWeight.Normal, 52)
    val TitleMediumExtraStrong = style(36, FontWeight.ExtraBold, 44)
    /** 날짜, 거리 87km, Seat Card 이름 */
    val TitleMediumStrong = style(36, FontWeight.Bold, 44)
    val TitleMediumNormal = style(36, FontWeight.Normal, 44)
    val TitleSmallExtraStrong = style(32, FontWeight.ExtraBold, 40)
    /** Route Point 장소, 탑승자 이름 */
    val TitleSmallStrong = style(32, FontWeight.Bold, 40)
    val TitleSmallNormal = style(32, FontWeight.Normal, 40)

    // Body
    /** Header 제목·이전·다음, Section Header 제목·개수, Button 라벨 */
    val BodyLargeStrong = style(30, FontWeight.Bold, 38)
    val BodyLargeNormal = style(30, FontWeight.Normal, 38)
    /** Banner 제목 */
    val BodyMediumStrong = style(28, FontWeight.Bold, 36)
    val BodyMediumNormal = style(28, FontWeight.Normal, 36)
    /** Highlight Row 제목, Status Pill, Tab Chip Active, List Row 제목, Milestone 거리 */
    val BodySmallStrong = style(26, FontWeight.Bold, 34)
    /** Text Button, Highlight Row 시각, Piece Tag, Popup 설명 */
    val BodySmallNormal = style(26, FontWeight.Normal, 34)

    // Label
    /** 날씨·온도, 시간, Route Point 시각, List Row 보조, Seat Card 자리 */
    val LabelMedium = style(24, FontWeight.Normal, 28)
    /** Tag, Banner 캡션, 누적 환산 한 줄, Milestone 캡션. Pleos 최소 크기 */
    val LabelSmall = style(20, FontWeight.Normal, 24)

    // 화면 전용 (Figma 스타일 없음)
    /** 주행 중 상태 문장 */
    val DrivingStatus = style(72, FontWeight.ExtraBold, 84)
    /** 일기 본문: Body/Medium/Normal 28에 줄 간격 52(종이 줄과 일치)로 덮어씀 */
    val DiaryBody = style(28, FontWeight.Normal, 52)
}
