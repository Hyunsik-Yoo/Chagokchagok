package com.hyunsikyoo.chagokchagok.feature.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.feature.common.SubText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHighlightRow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPieceTag
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPopup
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPopupScrim
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.PopupAction
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.painter

/**
 * 명장면 전체보기 (W-D2, 디자인 완료 · 노드 112:9723). 제목 + 그날 명장면 전체 수,
 * Highlight Row를 시간 순서대로 전부, [닫기] 하나. 정차 중에만.
 */
@Composable
fun RecordsPopup(state: DiaryUiState, onClose: () -> Unit) {
    CarDiaryPopupScrim(onDismiss = onClose) {
        CarDiaryPopup(
            title = "오늘의 명장면 ${state.records.size}",
            subtitle = "시간 순서대로 모두 보여 줄게",
            actions = listOf(PopupAction("닫기", onClose, ButtonStyle.Neutral)),
        ) {
            Column(
                Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                state.records.forEach { r ->
                    CarDiaryHighlightRow(time = r.time, text = r.title, emoji = r.emoji.char, icon = r.emoji.painter())
                }
                SubText("일기 화면 카드에는 위에서 3개만 보여요")
            }
        }
    }
}

/**
 * 조각 전체보기 (W-D3, 디자인 완료 · 노드 112:9889). 제목 + 그날 조각 전체 수, Piece Tag 전부 줄바꿈,
 * 맨 앞은 동네 이야기·맨 끝은 소나 컨디션, [닫기] 하나.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PiecesPopup(state: DiaryUiState, onClose: () -> Unit) {
    CarDiaryPopupScrim(onDismiss = onClose) {
        CarDiaryPopup(
            title = "오늘의 조각 ${state.allPieces.size}",
            subtitle = "시간이 없는 작은 기록이에요. 일기 글에는 넣지 않아요",
            actions = listOf(PopupAction("닫기", onClose, ButtonStyle.Neutral)),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap),
                verticalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap),
            ) {
                state.allPieces.forEach { CarDiaryPieceTag(it) }
            }
        }
    }
}
