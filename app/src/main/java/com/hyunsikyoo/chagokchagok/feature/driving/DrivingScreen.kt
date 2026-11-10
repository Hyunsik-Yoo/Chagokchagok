package com.hyunsikyoo.chagokchagok.feature.driving

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.domain.DiaryFormat
import com.hyunsikyoo.chagokchagok.domain.model.Highlight
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAppFrame
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHighlightRow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryRoute
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryRoutePoint
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySectionHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryStatusPill
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNav
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.StatusTone
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.painter
import kotlinx.coroutines.delay
import java.time.LocalDateTime

/** 주행 중 화면 상태 */
data class DrivingUiState(
    val fromPlace: String,
    val startAt: LocalDateTime,
    val destination: String?,
    val eta: LocalDateTime?,
    /** 지금까지 모인 명장면 (발생 순서대로) */
    val highlights: List<Highlight>,
    val resumed: Boolean,
    val recordingOff: Boolean,
    /** 주행 규제 최고 단계: 점 깜빡임·로딩 점도 멈춘다 */
    val strictRestriction: Boolean = false,
)

/**
 * 주행 중 화면 (W1, 디자인 완료 · Figma 56:3820). 기어 D일 때 보이는 유일한 화면.
 * "소나가 지금 기억하고 있다"만 전달, 2초 안에 읽힌다. 팝업·자동 스크롤·빠르게 바뀌는 숫자 없음.
 * 핸드오프 번호: 1 헤더 · 2 기록 중 알약 · 3 상태 문장 · 4 출발→목적지 · 5 지금까지 모인 명장면 · 6 기록 끄기
 */
@Composable
fun DrivingScreen(
    state: DrivingUiState,
    onBack: () -> Unit,
    onToggleRecording: () -> Unit,
) {
    CarDiaryAppFrame(
        header = {
            // 1 헤더: Back only. 뒤로 가기는 홈 대신 Pleos 앱 목록으로
            CarDiaryHeader(title = "소나의 일기", nav = HeaderNav.BackOnly, onBack = onBack)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 2 기록 중 알약
            CarDiaryStatusPill(
                label = if (state.recordingOff) "기록 꺼짐" else "기록 중", // W1-1: Tone=Neutral "기록 꺼짐"
                tone = if (state.recordingOff) StatusTone.Neutral else StatusTone.Accent,
                blinking = !state.recordingOff && !state.strictRestriction,
            )
            Box(Modifier.height(36.dp))
            // 3 상태 문장: 72px ExtraBold, 1줄 고정, 상태별 3종
            CarDiaryText(
                text = when {
                    state.recordingOff -> "오늘은 기록하지 않아요"
                    state.resumed -> "아까 길 이어서 기록하고 있어요"
                    else -> "소나가 오늘을 기억하고 있어요"
                },
                style = CarDiaryTypography.DrivingStatus,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Box(Modifier.height(40.dp))
            // 4 출발 → 목적지 (내비 목적지가 없으면 출발지만)
            CarDiaryRoute(
                from = { CarDiaryRoutePoint(state.fromPlace, DiaryFormat.time(state.startAt), "출발") },
                to = state.destination?.let { dest ->
                    { CarDiaryRoutePoint(dest, state.eta?.let(DiaryFormat::time) ?: "--:--", "도착 예정") }
                },
            )
            Box(Modifier.height(40.dp))
            // 5 지금까지 모인 명장면 (기록 끄기 당일 숨김)
            if (!state.recordingOff) {
                HighlightsSoFar(state.highlights, animate = !state.strictRestriction)
            }
        }
        // 6 기록 끄기 (오늘만) — 화면 유일 버튼, 확인 팝업 없음
        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CarDiaryButton(
                label = if (state.recordingOff) "다시 켜기" else "기록 끄기 (오늘만)",
                onClick = onToggleRecording, // 끈 직후 Toast "홈에서도 다시 켤 수 있어요"는 앱 루트가 띄운다
                style = ButtonStyle.Secondary,
            )
        }
    }
}

/** 최신이 맨 위(시간 역순), 최대 3줄. 3개면 높이 고정. 맨 아래 "..."은 로딩 표시(0개여도 유지) */
@Composable
private fun HighlightsSoFar(highlights: List<Highlight>, animate: Boolean) {
    val shown = highlights.sortedByDescending { it.at }.let { list ->
        // 4개째부터는 점수 최하 줄을 뺀다
        if (list.size <= 3) list else list.sortedByDescending { it.score }.take(3).sortedByDescending { it.at }
    }
    CarDiaryCard(Modifier.width(710.dp), shadow = CardShadow.Hard) {
        CarDiarySectionHeader(title = "지금까지 모인 명장면", count = highlights.size)
        if (shown.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(19.dp)) {
                shown.forEach { h ->
                    CarDiaryHighlightRow(
                        time = DiaryFormat.time(h.at),
                        text = h.title,
                        emoji = h.type.emoji.char,
                        icon = h.type.emoji.painter(),
                    )
                }
            }
        }
        LoadingDots(animate, Modifier.fillMaxWidth())
    }
}

/** 점 3개, 2초 주기 순차 점등 */
@Composable
private fun LoadingDots(animate: Boolean, modifier: Modifier = Modifier) {
    val phase = if (animate) {
        val transition = rememberInfiniteTransition(label = "loading-dots")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(tween(2_000, easing = LinearEasing), RepeatMode.Restart),
            label = "loading-dots-phase",
        )
        value
    } else {
        3f
    }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)) {
        repeat(3) { index ->
            Box(
                Modifier
                    .size(6.dp)
                    .alpha(if (phase >= index) 1f else 0.25f)
                    .background(CarDiaryColors.Line, CircleShape),
            )
        }
    }
}
