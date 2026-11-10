package com.hyunsikyoo.chagokchagok.feature.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.data.demo.DemoDiaryRepository
import com.hyunsikyoo.chagokchagok.data.tts.NarrationState
import com.hyunsikyoo.chagokchagok.domain.DiaryFormat
import com.hyunsikyoo.chagokchagok.domain.model.Diary
import com.hyunsikyoo.chagokchagok.feature.common.SubText
import com.hyunsikyoo.chagokchagok.feature.common.weatherEmoji
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonSize
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CalendarDayState
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAppFrame
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCalendarDay
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryIconButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySectionHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNav
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.painter
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth

/**
 * 일기 달력 (W4, 디자인 완료 · 노드 89:4836). 홈 [달력으로 보기]로 들어온다. 정차 중에만.
 * 핸드오프 번호: 1 헤더 · 2 월 이동 · 3 달력 7열(Diary/Empty/Selected + 오늘) · 4 고른 날 미리보기 · 5 이달의 일기
 * 긴 일기와 짧은 일기는 구분하지 않는다(11/8).
 */
@Composable
fun CalendarScreen(
    narration: NarrationState,
    onBack: () -> Unit,
    onOpenDiary: (Diary?) -> Unit,
    onPlay: (Diary) -> Unit,
) {
    val today = DemoDiaryRepository.today
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var selected by remember { mutableStateOf(DemoDiaryRepository.latest().date) }
    val summaries = DemoDiaryRepository.summaries.filterNot { it.hidden }.associateBy { it.date }

    CarDiaryAppFrame(header = { CarDiaryHeader(title = "일기 달력", nav = HeaderNav.BackOnly, onBack = onBack) }) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ColumnGap)) {
            CarDiaryCard(Modifier.weight(1f).fillMaxHeight(), shadow = CardShadow.Hard) {
                // 2 월 이동
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryIconButton(CarDiaryIcons.ChevronLeft, contentDescription = "이전 달", onClick = { month = month.minusMonths(1) })
                    CarDiaryText("${month.year}년 ${month.monthValue}월", style = CarDiaryTypography.TitleMediumStrong)
                    CarDiaryIconButton(CarDiaryIcons.ChevronRight, contentDescription = "다음 달", onClick = { month = month.plusMonths(1) })
                }
                // 3 달력 7열
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                            CarDiaryText(it, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        }
                    }
                    val offset = month.atDay(1).dayOfWeek.value % 7 // 일요일 시작
                    val weeks = (0 until 42).map { it - offset + 1 }.chunked(7).filter { w -> w.any { it in 1..month.lengthOfMonth() } }
                    weeks.forEach { week ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            week.forEach { day ->
                                if (day !in 1..month.lengthOfMonth()) {
                                    Spacer(Modifier.weight(1f).height(108.dp))
                                } else {
                                    val date = month.atDay(day)
                                    val s = summaries[date]
                                    CarDiaryCalendarDay(
                                        day = day,
                                        state = when {
                                            s == null -> CalendarDayState.Empty
                                            date == selected -> CalendarDayState.Selected
                                            else -> CalendarDayState.Diary
                                        },
                                        today = date == today,
                                        emoji = s?.firstEmoji?.painter(),
                                        onClick = if (s != null) ({ selected = date }) else null,
                                        modifier = Modifier.weight(1f).height(108.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Column(Modifier.width(CarDiaryDimens.ColumnSmall).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(CarDiaryDimens.ColumnGap)) {
                DayPreview(selected, narration, onOpenDiary, onPlay)
                MonthSummary(month)
            }
        }
    }
}

/** 4 고른 날 미리보기: 제목, 날짜·날씨, 경로, 거리·시간, 앞 두 줄, [들려주기] [일기 보기] Small */
@Composable
private fun DayPreview(date: LocalDate, narration: NarrationState, onOpenDiary: (Diary?) -> Unit, onPlay: (Diary) -> Unit) {
    val summary = DemoDiaryRepository.summaries.firstOrNull { it.date == date }
    val diary = DemoDiaryRepository.diary(date)
    CarDiaryCard(Modifier.fillMaxWidth()) {
        if (summary == null || diary == null) {
            CarDiaryText("이 날은 함께 달린 기록이 없어요", style = CarDiaryTypography.BodySmallStrong)
            return@CarDiaryCard
        }
        val first = diary.trips.first()
        val last = diary.trips.last()
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CarDiaryText(diary.title, style = CarDiaryTypography.TitleMediumStrong, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CarDiaryText(DiaryFormat.date(date), style = CarDiaryTypography.LabelMedium)
                Box(Modifier.width(1.dp).height(18.dp).background(CarDiaryColors.LineSoft))
                Image(weatherEmoji(summary.weather).painter(), contentDescription = null, modifier = Modifier.size(28.dp))
                CarDiaryText(summary.weather, style = CarDiaryTypography.LabelMedium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                CarDiaryText(
                    "${first.startPlace.name} ${DiaryFormat.time(first.startAt)} → ${last.endPlace?.name.orEmpty()} ${DiaryFormat.time(diary.highlights.maxOfOrNull { it.at } ?: last.endAt)}",
                    style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub, maxLines = 1,
                )
                CarDiaryText(
                    "${DiaryFormat.distance(diary.trips.sumOf { it.distanceKm })}  |  ${DiaryFormat.duration(Duration.between(first.startAt, last.endAt))}",
                    style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub,
                )
            }
        }
        CarDiaryText(diary.sentences.joinToString(" "), style = CarDiaryTypography.BodySmallNormal.copy(lineHeight = CarDiaryTypography.DiaryBody.lineHeight * 0.85f), maxLines = 2)
        val playing = narration.playing(date.toString())
        Row(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ButtonGap)) {
            CarDiaryButton(
                if (playing) narration.elapsedLabel else "들려주기",
                onClick = { onPlay(diary) },
                style = ButtonStyle.Primary,
                size = ButtonSize.Small,
                icon = if (playing) CarDiaryIcons.Pause else CarDiaryIcons.Play,
            )
            CarDiaryButton("일기 보기", onClick = { onOpenDiary(diary) }, style = ButtonStyle.Secondary, size = ButtonSize.Small)
        }
    }
}

/** 5 이달의 일기: 일기 수, 달린 거리, 가장 멀리, 가장 자주 */
@Composable
private fun MonthSummary(month: YearMonth) {
    val inMonth = DemoDiaryRepository.summaries.filter { YearMonth.from(it.date) == month && !it.hidden }
    CarDiaryCard(Modifier.fillMaxWidth()) {
        CarDiarySectionHeader("이달의 일기", leadingIcon = CarDiaryIcons.Calendar)
        if (inMonth.isEmpty()) {
            SubText("이 달엔 아직 일기가 없어요")
            return@CarDiaryCard
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CarDiaryText("일기", style = CarDiaryTypography.TitleMediumStrong)
                CarDiaryText("${inMonth.size}편", style = CarDiaryTypography.TitleMediumStrong, color = CarDiaryColors.Main)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CarDiaryText("이달 함께 달린 길", style = CarDiaryTypography.BodySmallNormal)
                CarDiaryText("${inMonth.sumOf { it.distanceKm }}km", style = CarDiaryTypography.BodySmallStrong, color = CarDiaryColors.Main)
            }
        }
        val far = inMonth.maxBy { it.distanceKm }
        val often = inMonth.groupBy { it.place }.maxBy { it.value.size }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SubText("가장 멀리")
                CarDiaryText("${far.place} ${far.distanceKm}km (${far.date.monthValue}/${far.date.dayOfMonth})", style = CarDiaryTypography.LabelMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SubText("가장 자주")
                CarDiaryText("${often.key} ${often.value.size}번", style = CarDiaryTypography.LabelMedium)
            }
        }
    }
}
