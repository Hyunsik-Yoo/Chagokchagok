package com.hyunsikyoo.chagokchagok.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.data.demo.DemoDiaryRepository
import com.hyunsikyoo.chagokchagok.data.demo.DiarySummary
import com.hyunsikyoo.chagokchagok.data.tts.NarrationState
import com.hyunsikyoo.chagokchagok.domain.DiaryFormat
import com.hyunsikyoo.chagokchagok.domain.SonaLine
import com.hyunsikyoo.chagokchagok.domain.model.Diary
import com.hyunsikyoo.chagokchagok.domain.model.FluentEmoji
import com.hyunsikyoo.chagokchagok.feature.common.Paperclip
import com.hyunsikyoo.chagokchagok.feature.common.SubText
import com.hyunsikyoo.chagokchagok.feature.common.weatherEmoji
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.AvatarSize
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonSize
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAppFrame
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAvatar
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryBanner
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryProgressBar
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySectionHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNav
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNavItem
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.FluentSticker
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.painter
import java.time.Duration
import java.time.format.TextStyle
import java.util.Locale

/**
 * 홈 (W2, 디자인 완료 · 노드 61:4097 / W2-1 110:8243). 정차 중 앱을 열면 처음 보는 화면.
 * 핸드오프 번호: 1 헤더(Mix: 기록 시작·설정) · 2 가장 최근 일기 · 3 지난 일기 + 달력보기 · 4 1년 전 오늘 · 5 함께 달린 길 · 6 소나의 한마디
 */
@Composable
fun HomeScreen(
    carName: String,
    childName: String,
    readyToRecord: Boolean,
    narration: NarrationState,
    onStartRecording: () -> Unit,
    onOpenDiary: (Diary?) -> Unit,
    onPlay: (Diary) -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSpecialDay: () -> Unit,
    showSpecialDay: Boolean,
) {
    val diaries = DemoDiaryRepository.diaries()
    val latest = if (diaries.isEmpty()) null else DemoDiaryRepository.latest()
    val summaries = DemoDiaryRepository.summaries
    CarDiaryAppFrame(
        header = {
            // 1 헤더 Nav=Mix: [기록 시작]은 탑승 확인(W3) 뒤 "기록 준비됨"(W2-1), [설정]은 W9
            // 첫 화면이라 뒤로 가기는 뺀다(11/9 현식). 나가기는 Pleos 하단 메뉴로
            CarDiaryHeader(
                title = "${carName}의 일기장",
                nav = HeaderNav.Mix,
                showBack = false,
                actions = listOf(
                    HeaderNavItem(if (readyToRecord) "기록 준비됨" else "기록 시작", onStartRecording),
                    HeaderNavItem("설정", onOpenSettings, icon = CarDiaryIcons.Settings),
                ),
            )
        },
    ) {
        if (latest == null) {
            EmptyHome(carName, childName)
            return@CarDiaryAppFrame
        }
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ColumnGap)) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(40.dp)) {
                LatestDiaryCard(latest, narration, onOpen = { onOpenDiary(latest) }, onPlay = { onPlay(latest) })
                PastDiaries(
                    summaries.filter { it.date != latest.date }, // 가장 최근 일기는 위 카드에 이미 있다
                    showCalendarLink = diaries.size > 3, // 일기 3편 이하일 땐 숨김
                    onOpenCalendar = onOpenCalendar,
                    onOpen = { s -> onOpenDiary(DemoDiaryRepository.diary(s.date)) },
                )
            }
            Column(Modifier.width(CarDiaryDimens.ColumnSmall).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(28.dp)) {
                // 4 1년 전 오늘 배너 (작년 같은 날 일기가 있을 때만)
                if (showSpecialDay) {
                    CarDiaryBanner(
                        caption = "1년 전 오늘",
                        title = "우리 ${DemoDiaryRepository.yearAgo.trips.last().endPlace?.name}에 다녀왔어",
                        onClick = onOpenSpecialDay,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                MileageCard()
                SonaSays(
                    message = SonaLine.pick(DemoDiaryRepository.today, carName, childName, summaries),
                    carName = carName,
                    onStartRecording = onStartRecording,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }
    }
}

/** 2 가장 최근 일기 카드 (Card Hard + 클립) */
@Composable
private fun LatestDiaryCard(diary: Diary, narration: NarrationState, onOpen: () -> Unit, onPlay: () -> Unit) {
    val first = diary.trips.first()
    val last = diary.trips.last()
    val playing = narration.playing(diary.date.toString())
    val passengers = diary.trips.flatMap { it.passengers }.distinctBy { it.id }
    Box(Modifier.fillMaxWidth()) {
        CarDiaryCard(Modifier.fillMaxWidth(), shadow = CardShadow.Hard) {
            CarDiarySectionHeader("가장 최근 일기")
            CarDiaryText(diary.title, style = CarDiaryTypography.HeadlineSmallStrong, maxLines = 1)
            // 날짜·날씨·경로 상자
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(CarDiaryColors.SkyBackground, CarDiaryShapes.ListRow)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarDiaryText(DiaryFormat.date(diary.date), style = CarDiaryTypography.BodySmallNormal)
                    Box(Modifier.width(1.dp).height(20.dp).background(CarDiaryColors.LineSoft))
                    Image(weatherEmoji(diary.weather?.label).painter(), contentDescription = null, modifier = Modifier.size(30.dp))
                    CarDiaryText(diary.weather?.label.orEmpty(), style = CarDiaryTypography.BodySmallNormal)
                }
                CarDiaryText(
                    "${first.startPlace.name} ${DiaryFormat.time(first.startAt)} → ${last.endPlace?.name.orEmpty()} ${DiaryFormat.time(diary.highlights.maxOfOrNull { it.at } ?: last.endAt)}" +
                        "  |  ${DiaryFormat.distance(diary.trips.sumOf { it.distanceKm })}  |  ${DiaryFormat.duration(Duration.between(first.startAt, last.endAt))}",
                    style = CarDiaryTypography.LabelSmall,
                    color = CarDiaryColors.TextSub,
                    maxLines = 1,
                )
            }
            // 본문 앞 3줄
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                diary.sentences.take(3).forEach {
                    CarDiaryText(it, style = CarDiaryTypography.BodyMediumNormal.copy(lineHeight = CarDiaryTypography.DiaryBody.lineHeight * 0.8f), maxLines = 1)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ButtonGap)) {
                CarDiaryButton(
                    label = if (playing) narration.elapsedLabel else "들려주기",
                    onClick = onPlay,
                    style = ButtonStyle.Primary,
                    icon = if (playing) CarDiaryIcons.Pause else CarDiaryIcons.Play,
                )
                CarDiaryButton("일기 보기", onClick = onOpen, style = ButtonStyle.Secondary)
                Spacer(Modifier.weight(1f))
                if (passengers.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.width(30.dp * (passengers.size.coerceAtMost(3) - 1) + 40.dp).height(40.dp)) {
                            repeat(passengers.size.coerceAtMost(3)) { i -> CarDiaryAvatar(Modifier.offset(x = 30.dp * i), size = AvatarSize.Small) }
                        }
                        CarDiaryText("${DiaryFormat.names(passengers.map { it.name })}과 함께", style = CarDiaryTypography.LabelMedium, maxLines = 1)
                    }
                }
            }
        }
        Paperclip(Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = (-28).dp))
    }
}

/**
 * 3 지난 일기 + 달력보기 (최종 기획서 11/10: 월 탭 삭제).
 * 최근 일기부터 카드 4장, 넘치면 가로 스크롤. 짧은 일기도 같은 카드.
 * [달력보기]는 Button Small Neutral + 달력 아이콘 → W4. 일기 3편 이하일 땐 숨김.
 */
@Composable
private fun PastDiaries(
    summaries: List<DiarySummary>,
    showCalendarLink: Boolean,
    onOpenCalendar: () -> Unit,
    onOpen: (DiarySummary) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CarDiaryText("지난 일기", style = CarDiaryTypography.BodyLargeStrong)
            Spacer(Modifier.weight(1f))
            if (showCalendarLink) {
                CarDiaryButton(
                    "달력보기",
                    onClick = onOpenCalendar,
                    style = ButtonStyle.Neutral,
                    size = ButtonSize.Small,
                    icon = CarDiaryIcons.Calendar,
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            summaries.filterNot { it.hidden }.sortedByDescending { it.date }.forEach { s ->
                PastDiaryCard(s, onClick = { onOpen(s) })
            }
        }
    }
}

@Composable
private fun PastDiaryCard(s: DiarySummary, onClick: () -> Unit) {
    val dow = s.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    Column(
        Modifier
            .width(246.dp)
            .background(CarDiaryColors.Card, CarDiaryShapes.Card)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(weatherEmoji(s.weather).painter(), contentDescription = null, modifier = Modifier.size(40.dp))
            CarDiaryText(s.weather, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub, maxLines = 1)
        }
        CarDiaryText("${s.date.monthValue}월 ${s.date.dayOfMonth}일 $dow", style = CarDiaryTypography.BodyLargeStrong, maxLines = 1)
        SubText(s.place)
        SubText("${s.distanceKm}km  |  명장면 ${s.highlightCount}")
    }
}

/** 5 함께 달린 길 — 보여 주기만 한다(누르는 곳 없음, W5 범위 제외) */
@Composable
private fun MileageCard() {
    val mileage = DemoDiaryRepository.mileage()
    val remain = mileage.nextMilestoneKm - mileage.totalKm
    CarDiaryCard(Modifier.fillMaxWidth()) {
        CarDiarySectionHeader("우리가 함께 달린 길")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CarDiaryText(DiaryFormat.distance(mileage.totalKm), style = CarDiaryTypography.HeadlineMediumExtraStrong)
            SubText("서울 ↔ 부산 ${mileage.totalKm / 800}번 왕복한 만큼")
            CarDiaryProgressBar(DiaryFormat.mileageProgress(mileage), modifier = Modifier.fillMaxWidth())
            CarDiaryText("다음 ${DiaryFormat.distance(mileage.nextMilestoneKm)}까지 ${DiaryFormat.distance(remain)}", style = CarDiaryTypography.BodySmallStrong)
            CarDiaryText(
                "함께 쓴 일기 ${DemoDiaryRepository.DIARY_COUNT}편  |  첫 일기 ${DemoDiaryRepository.firstDiaryDate.monthValue}월 ${DemoDiaryRepository.firstDiaryDate.dayOfMonth}일",
                style = CarDiaryTypography.LabelSmall,
                color = CarDiaryColors.TextSub,
            )
        }
    }
}

/** 6 소나의 한마디: 말풍선(최대 3줄) + [기록 시작] Small + 소나 아바타. 꼬리는 왼쪽 아래 */
@Composable
private fun SonaSays(message: String, carName: String, onStartRecording: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box {
            Column(
                Modifier
                    .fillMaxWidth()
                    .hardShadow(CarDiaryShadows.Card, CarDiaryShapes.Card)
                    .background(CarDiaryColors.SkyBackground, CarDiaryShapes.Card)
                    .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
                    .padding(start = 34.dp, end = 30.dp, top = 30.dp, bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                CarDiaryText("\"$message\"", style = CarDiaryTypography.BodyLargeStrong, maxLines = 3)
                CarDiaryButton("기록 시작", onClick = onStartRecording, style = ButtonStyle.Secondary, size = ButtonSize.Small, modifier = Modifier.align(Alignment.End))
            }
            BubbleTail(Modifier.align(Alignment.BottomStart).offset(x = 44.dp, y = 28.dp))
        }
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarDiaryAvatar()
            CarDiaryText("${carName}의 한마디", style = CarDiaryTypography.BodySmallStrong)
        }
    }
}

/** 말풍선 꼬리: 말풍선 테두리 아랫선을 덮고, 빗변 두 개만 검정 선 */
@Composable
private fun BubbleTail(modifier: Modifier) {
    Canvas(modifier.size(width = 44.dp, height = 30.dp)) {
        val stroke = CarDiaryDimens.Stroke.toPx()
        val top = 0f
        val a = Offset(0f, top)
        val tip = Offset(0f, size.height)
        val b = Offset(size.width, top)
        val path = Path().apply { moveTo(a.x, a.y); lineTo(tip.x, tip.y); lineTo(b.x, b.y); close() }
        drawPath(path, CarDiaryColors.SkyBackground)
        drawLine(CarDiaryColors.Line, a, tip, stroke)
        drawLine(CarDiaryColors.Line, tip, b, stroke)
    }
}

/** 처음 설치 홈 (W2-0, 일기 0편) */
@Composable
private fun EmptyHome(carName: String, childName: String) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        FluentSticker(FluentEmoji.Sparkles)
        CarDiaryText("${childName}이랑 첫 나들이를 떠나 보세요.", style = CarDiaryTypography.HeadlineSmallStrong)
        CarDiaryText("도착하면 ${carName}가 일기를 써 줄게요", style = CarDiaryTypography.BodyLargeNormal)
    }
}

