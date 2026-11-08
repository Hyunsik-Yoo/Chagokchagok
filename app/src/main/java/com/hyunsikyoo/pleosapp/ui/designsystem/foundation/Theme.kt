package com.hyunsikyoo.pleosapp.ui.designsystem.foundation

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

/** 화면 안 기본 글자 스타일. [com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryText]가 읽는다 */
val LocalCarDiaryTextStyle = staticCompositionLocalOf { CarDiaryTypography.BodyLargeNormal }

/**
 * 차 일기 테마. Material 테마를 쓰지 않고 팀 디자인 시스템 토큰만 쓴다.
 * - 눌림 효과는 물결(ripple) 대신 각 컴포넌트가 정한 방식(버튼 내려앉기 등)만 쓴다.
 * - 다크 모드 없음.
 */
@Composable
fun CarDiaryTheme(
    textStyle: TextStyle = CarDiaryTypography.BodyLargeNormal,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalCarDiaryTextStyle provides textStyle,
        LocalIndication provides NoIndication,
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = CarDiaryColors.Main,
            backgroundColor = CarDiaryColors.SkyBackground,
        ),
        content = content,
    )
}
