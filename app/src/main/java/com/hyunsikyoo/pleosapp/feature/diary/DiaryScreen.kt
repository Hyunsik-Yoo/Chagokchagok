package com.hyunsikyoo.pleosapp.feature.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunsikyoo.pleosapp.domain.model.Sticker
import com.hyunsikyoo.pleosapp.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CardShadow
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryAppFrame
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryAvatarStack
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryCard
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryHeader
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryHighlightRow
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryPieceTag
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryProgressBar
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryRoute
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryRoutePoint
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiarySectionHeader
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.pleosapp.ui.designsystem.component.HeaderNav
import com.hyunsikyoo.pleosapp.ui.designsystem.component.HeaderNavItem
import com.hyunsikyoo.pleosapp.ui.designsystem.component.PieceTagStyle
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.FluentSticker
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.painter

/**
 * 일기 화면 (와이어프레임 C, 디자인 완료 · Figma 24:3501). 정차 중에만.
 * 핸드오프 번호: 1 헤더 · 2 날짜·날씨 · 3 경로 · 4 거리·시간 · 5 함께 탄 사람 · 6 제목 · 7 일기 글+형광펜 · 8 스티커
 * · 9 들려주기 · 10 폰으로 보내기 · 11 오늘의 명장면 · 12 오늘의 조각 · 13 우리가 함께 달린 길
 */
@Composable
fun DiaryRoute(
    date: java.time.LocalDate? = null,
    onBack: () -> Unit = {},
    onPassengersClick: () -> Unit = {},
    onSendToPhone: () -> Unit = {},
    viewModel: DiaryViewModel = viewModel(),
) {
    androidx.compose.runtime.LaunchedEffect(date) { viewModel.select(date) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val popup by viewModel.popup.collectAsStateWithLifecycle()
    DiaryScreen(
        state = state,
        popup = popup,
        onBack = onBack,
        onPrev = viewModel::showPrev,
        onNext = viewModel::showNext,
        onPassengersClick = onPassengersClick,
        onToggleNarration = viewModel::toggleNarration,
        onSendToPhone = onSendToPhone,
        onOpenPopup = viewModel::openPopup,
        onClosePopup = viewModel::closePopup,
        onNarrateRecords = viewModel::narrateRecords,
    )
}

@Composable
fun DiaryScreen(
    state: DiaryUiState,
    popup: DiaryPopup,
    onBack: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onPassengersClick: () -> Unit,
    onToggleNarration: () -> Unit,
    onSendToPhone: () -> Unit,
    onOpenPopup: (DiaryPopup) -> Unit,
    onClosePopup: () -> Unit,
    onNarrateRecords: () -> Unit,
) {
    CarDiaryAppFrame(
        header = {
            // 1 헤더: Nav=Both. 뒤로 가기는 홈, 이전·다음은 날짜순 앞뒤 일기
            CarDiaryHeader(
                title = "소나의 일기",
                nav = HeaderNav.Both,
                onBack = onBack,
                prev = HeaderNavItem("이전 일기", onPrev, enabled = state.hasPrev),
                next = HeaderNavItem("다음 일기", onNext, enabled = state.hasNext),
            )
        },
        overlay = when (popup) {
            DiaryPopup.Records -> { { RecordsPopup(state, onClose = onClosePopup) } }
            DiaryPopup.Pieces -> { { PiecesPopup(state, onClose = onClosePopup) } }
            DiaryPopup.None -> null
        },
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(40.dp)) {
            SummaryRow(state, onPassengersClick)
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                DiaryPaper(
                    state = state,
                    onToggleNarration = onToggleNarration,
                    onSendToPhone = onSendToPhone,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                SideColumn(
                    state = state,
                    onOpenPopup = onOpenPopup,
                    modifier = Modifier.width(CarDiaryDimens.ColumnSmallWide - 11.dp).fillMaxHeight(),
                )
            }
        }
    }
}

/** 2~5번 요약 한 줄. 값 없는 칸은 숨기고 왼쪽 정렬 */
@Composable
private fun SummaryRow(state: DiaryUiState, onPassengersClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(84.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 2 날짜·날씨
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CarDiaryText(state.dateLabel, style = CarDiaryTypography.TitleMediumStrong, maxLines = 1)
            if (state.weatherLabel != null || state.temperatureLabel != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.weatherLabel?.let { CarDiaryText(it, style = CarDiaryTypography.LabelMedium) }
                    if (state.weatherLabel != null && state.temperatureLabel != null) {
                        Box(Modifier.width(1.dp).height(20.dp).background(CarDiaryColors.LineSoft))
                    }
                    state.temperatureLabel?.let {
                        CarDiaryText(it, style = CarDiaryTypography.LabelMedium.copy(fontWeight = CarDiaryTypography.BodySmallStrong.fontWeight))
                    }
                }
            }
        }
        SummaryDivider()
        // 3 경로
        CarDiaryRoute(
            from = { CarDiaryRoutePoint(state.from.place, state.from.time, state.from.tag) },
            to = state.to?.let { to -> { CarDiaryRoutePoint(to.place, to.time, to.tag) } },
        )
        Spacer(Modifier.width(80.dp))
        // 4 거리·시간
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CarDiaryText(state.distanceLabel, style = CarDiaryTypography.TitleMediumStrong, maxLines = 1)
            CarDiaryText(state.durationLabel, style = CarDiaryTypography.LabelMedium, maxLines = 1)
        }
        // 5 함께 탄 사람 — 탭하면 탑승 팝업(W3)을 다시 열어 고친다
        if (state.passengerNames != null) {
            SummaryDivider()
            Row(
                modifier = Modifier.clickable(role = Role.Button, onClickLabel = "함께 탄 사람 고치기", onClick = onPassengersClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CarDiaryAvatarStack(count = state.passengerCount.coerceAtMost(3), step = 46.dp)
                CarDiaryText(state.passengerNames, style = CarDiaryTypography.TitleSmallNormal, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        Modifier
            .padding(horizontal = 40.dp)
            .width(1.dp)
            .height(56.dp)
            .background(CarDiaryColors.LineSoft),
    )
}

/** 6~10번 일기 종이 (Card Hard — 이 화면의 가장 중요한 카드) */
@Composable
private fun DiaryPaper(
    state: DiaryUiState,
    onToggleNarration: () -> Unit,
    onSendToPhone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarDiaryCard(modifier = modifier, shadow = CardShadow.Hard) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val paperWidth = maxWidth
            val paperHeight = maxHeight
            Column(Modifier.fillMaxSize()) {
                // 6 제목
                CarDiaryText(state.title, style = CarDiaryTypography.HeadlineSmallExtraStrong, maxLines = 1)
                Spacer(Modifier.height(20.dp))
                // 7 일기 글 + 종이 줄. 오른쪽 띠(StickerLane)는 스티커 자리라 글을 넣지 않는다
                PaperLines(Modifier.weight(1f).fillMaxWidth()) {
                    BasicText(
                        text = diaryText(state.sentences, state.narration.sentenceIndex.takeIf { state.narration.playing(state.date.toString()) }),
                        style = CarDiaryTypography.DiaryBody,
                        modifier = Modifier.padding(end = StickerLane),
                    )
                }
                Spacer(Modifier.height(24.dp))
                // 9 들려주기 · 10 폰으로 보내기
                Row(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ButtonGap)) {
                    if (state.ttsAvailable) {
                        // W-D1: 읽는 중이면 멈춤 아이콘 + 지난 시간
                        CarDiaryButton(
                            label = if (state.narration.playing(state.date.toString())) state.narration.elapsedLabel else "들려주기",
                            onClick = onToggleNarration,
                            style = ButtonStyle.Primary,
                            icon = if (state.narration.playing(state.date.toString())) CarDiaryIcons.Pause else CarDiaryIcons.Play,
                        )
                    }
                    CarDiaryButton(
                        label = if (state.ttsAvailable) "폰으로 보내기" else "폰에서 듣기",
                        onClick = onSendToPhone,
                        style = ButtonStyle.Secondary,
                        icon = if (state.ttsAvailable) CarDiaryIcons.Send else CarDiaryIcons.Smartphone,
                    )
                }
            }
            // 8 스티커: 글을 가리지 않는 고정 빈자리
            state.stickers.forEach { sticker ->
                val (x, y) = stickerSlot(sticker, paperWidth, paperHeight)
                FluentSticker(sticker.emoji, Modifier.offset(x, y))
            }
        }
    }
}

/** 오른쪽 스티커 띠 폭 */
private val StickerLane = 150.dp

/** 스티커 고정 빈자리 5곳: 0 띠 맨 위(제목 줄) · 1 띠 가운데 · 2 띠 아래 · 3 글 아래 왼쪽 · 4 버튼 오른쪽 */
private fun stickerSlot(sticker: Sticker, width: Dp, height: Dp): Pair<Dp, Dp> = when (sticker.slot % 5) {
    0 -> width - 140.dp to (-16).dp
    1 -> width - 136.dp to height * 0.30f
    2 -> width - 136.dp to height * 0.52f
    3 -> 40.dp to height - 290.dp
    else -> width - 160.dp to height - 140.dp
}

/** 종이 줄: 글 길이와 무관하게 종이 끝까지, 줄 간격 52 */
@Composable
private fun PaperLines(modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier.drawBehind {
            val step = 52.dp.toPx()
            val stroke = 2.dp.toPx()
            var y = step
            while (y <= size.height) {
                drawLine(CarDiaryColors.LineLight, Offset(0f, y - stroke / 2), Offset(size.width, y - stroke / 2), stroke)
                y += step
            }
        },
    ) { content() }
}

/** 문장들을 이어 붙이고, 들려주기 중인 문장에만 형광펜(#FDF7C4) */
private fun diaryText(sentences: List<String>, reading: Int?): AnnotatedString = buildAnnotatedString {
    sentences.forEachIndexed { index, sentence ->
        if (index > 0) append(' ')
        if (index == reading) {
            withStyle(SpanStyle(background = CarDiaryColors.YellowBackground)) { append(sentence) }
        } else {
            append(sentence)
        }
    }
}

/** 11~13번 오른쪽 칸 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SideColumn(
    state: DiaryUiState,
    onOpenPopup: (DiaryPopup) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(32.dp)) {
        // 11 오늘의 명장면 (시간순, 이른 것이 위)
        CarDiaryCard(Modifier.fillMaxWidth()) {
            CarDiarySectionHeader(
                title = "오늘의 명장면",
                count = state.highlights.size,
                onMore = if (state.showAllRecords) ({ onOpenPopup(DiaryPopup.Records) }) else null,
            )
            if (state.highlights.isEmpty()) {
                CarDiaryText("오늘도 같이 달렸어", style = CarDiaryTypography.BodySmallStrong)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    state.highlights.forEach { h ->
                        CarDiaryHighlightRow(
                            time = h.time,
                            text = h.title,
                            emoji = h.emoji.char,
                            icon = h.emoji.painter(),
                            modifier = Modifier.clickable(role = Role.Button) { onOpenPopup(DiaryPopup.Records) },
                        )
                    }
                }
            }
        }
        // 12 오늘의 조각 (0개면 카드 숨기고 아래 카드를 올린다)
        if (state.allPieces.isNotEmpty()) {
            CarDiaryCard(Modifier.fillMaxWidth()) {
                CarDiarySectionHeader(
                    title = "오늘의 조각",
                    count = state.allPieces.size,
                    onMore = { onOpenPopup(DiaryPopup.Pieces) },
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap),
                    verticalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap),
                ) {
                    state.pieces.forEach { CarDiaryPieceTag(it) }
                    if (state.pieceMoreCount > 0) {
                        CarDiaryPieceTag("+${state.pieceMoreCount}", style = PieceTagStyle.More, onClick = { onOpenPopup(DiaryPopup.Pieces) })
                    }
                }
            }
        }
        // 13 우리가 함께 달린 길
        CarDiaryCard(Modifier.fillMaxWidth()) {
            CarDiaryText("우리가 함께 달린 길", style = CarDiaryTypography.BodyLargeStrong)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CarDiaryText(state.mileageLabel, style = CarDiaryTypography.HeadlineMediumExtraStrong)
                CarDiaryProgressBar(
                    progress = state.mileageProgress,
                    contentDescription = state.mileageCaption,
                    modifier = Modifier.fillMaxWidth(),
                )
                CarDiaryText(state.mileageCaption, style = CarDiaryTypography.LabelSmall)
            }
        }
    }
}
