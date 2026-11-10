package com.hyunsikyoo.chagokchagok.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens

/**
 * 화면 틀 (Pleos 앱 창). 앱 창 1700×1184는 시스템이 잡아 주고(x840 y96, 모서리 40),
 * 앱은 그 안을 #99D6FF로 칠한 뒤 헤더(110) + 내용(안쪽 여백 40)을 놓는다.
 * 팝업은 [overlay]에 [CarDiaryPopupScrim]으로 얹는다.
 */
@Composable
fun CarDiaryAppFrame(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxSize().background(CarDiaryColors.AppBackground)) {
        Column(Modifier.fillMaxSize()) {
            header()
            Box(
                Modifier
                    .weight(1f)
                    .padding(CarDiaryDimens.PagePadding),
                content = content,
            )
        }
        overlay?.invoke(this)
    }
}
