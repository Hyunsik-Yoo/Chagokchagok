package com.hyunsikyoo.chagokchagok.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CalendarDayState
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAppFrame
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAvatar
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAvatarStack
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryBanner
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCalendarDay
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHighlightRow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryIcon
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryIconButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryListRow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryMilestoneCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPieceTag
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPlaceholder
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPopup
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryPopupScrim
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryProgressBar
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryRoute
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryRoutePoint
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySeatCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySectionHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryStatusPill
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryTabChip
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryTag
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryTextButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryToggle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNav
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNavItem
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ListRowTrailing
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.PieceTagStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.PopupAction
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.StatusTone
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.domain.model.FluentEmoji
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.painter

/**
 * 디자인 시스템 카탈로그 (개발 확인용). 토큰과 컴포넌트 22개를 Figma "🧩 Components" 페이지 순서로 늘어놓는다.
 * 실제 화면을 만들기 전까지 앱 첫 화면으로 쓰고, 화면 구현이 시작되면 설정의 개발 메뉴로 옮긴다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignSystemCatalog(modifier: Modifier = Modifier) {
    var showPopup by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableIntStateOf(0) }
    var toggleA by remember { mutableStateOf(false) }
    var toggleB by remember { mutableStateOf(true) }

    CarDiaryAppFrame(
        modifier = modifier,
        header = {
            CarDiaryHeader(
                title = "소나의 일기 · 디자인 시스템",
                nav = HeaderNav.Both,
                prev = HeaderNavItem("이전 일기", onClick = {}),
                next = HeaderNavItem("다음 일기", onClick = {}, enabled = false),
            )
        },
        overlay = if (showPopup) {
            {
                CarDiaryPopupScrim(onDismiss = { showPopup = false }) {
                    CarDiaryPopup(
                        title = "팝업 제목",
                        subtitle = "한 줄 설명. 맞으면 그대로 두면 돼요",
                        onClose = { showPopup = false },
                        actions = listOf(
                            PopupAction("닫기", { showPopup = false }, ButtonStyle.Neutral),
                            PopupAction("확인", { showPopup = false }, ButtonStyle.Primary),
                        ),
                    ) {
                        CarDiaryPlaceholder("내용 자리 (자리 4칸, 지도+후보, QR, 그날 그림…)", Modifier.fillMaxSize())
                    }
                }
            }
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(48.dp),
        ) {
            Section("색 토큰") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    listOf(
                        "app-bg" to CarDiaryColors.AppBackground, "main" to CarDiaryColors.Main,
                        "primary" to CarDiaryColors.Primary, "accent-link" to CarDiaryColors.AccentLink,
                        "green" to CarDiaryColors.Green, "green-bg" to CarDiaryColors.GreenBackground,
                        "emerald" to CarDiaryColors.Emerald, "yellow-bg" to CarDiaryColors.YellowBackground,
                        "sky-bg" to CarDiaryColors.SkyBackground, "piece-bg" to CarDiaryColors.PieceBackground,
                        "surface" to CarDiaryColors.Surface, "surface-2" to CarDiaryColors.Surface2,
                        "line-soft" to CarDiaryColors.LineSoft, "line-light" to CarDiaryColors.LineLight,
                        "disabled-fill" to CarDiaryColors.DisabledFill, "text-disabled" to CarDiaryColors.TextDisabled,
                    ).forEach { (name, color) -> Swatch(name, color) }
                }
                CarDiaryText("Neobrutalism 팔레트 (main / background)", style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarDiaryColors.Palette.all.forEach { (name, main, bg) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row {
                                Box(Modifier.size(44.dp).background(main))
                                Box(Modifier.size(44.dp).background(bg))
                            }
                            CarDiaryText(name, style = CarDiaryTypography.LabelSmall)
                        }
                    }
                }
            }

            Section("글자 스타일 (Asta Sans)") {
                listOf<Pair<String, TextStyle>>(
                    "Headline/Medium/Extra_Strong 56 · 3,214km" to CarDiaryTypography.HeadlineMediumExtraStrong,
                    "Headline/Small/Extra_Strong 48 · 할머니 댁에 다녀온 날" to CarDiaryTypography.HeadlineSmallExtraStrong,
                    "Title/Medium/Strong 36 · 11월 1일 일요일" to CarDiaryTypography.TitleMediumStrong,
                    "Title/Small/Strong 32 · 우리 집" to CarDiaryTypography.TitleSmallStrong,
                    "Body/Large/Strong 30 · 오늘의 명장면" to CarDiaryTypography.BodyLargeStrong,
                    "Body/Medium/Strong 28 · 우리 강릉 바다에 갔었어" to CarDiaryTypography.BodyMediumStrong,
                    "Body/Small/Strong 26 · 비가 오기 시작했어" to CarDiaryTypography.BodySmallStrong,
                    "Body/Small/Normal 26 · 전체보기" to CarDiaryTypography.BodySmallNormal,
                    "Label/Medium 24 · 비 온 뒤 갬 │ 14℃" to CarDiaryTypography.LabelMedium,
                    "Label/Small 20 · 서울↔부산 4번 왕복" to CarDiaryTypography.LabelSmall,
                ).forEach { (label, style) -> CarDiaryText(label, style = style) }
                CarDiaryText("소나가 오늘을 기억하고 있어요", style = CarDiaryTypography.DrivingStatus)
                CarDiaryText(
                    "일기 본문 28 / 줄 간격 52 — 오늘은 아빠, 엄마, 준이랑 할머니 댁에 다녀왔어. 가는 길에 비가 와서 와이퍼가 바빴어.",
                    style = CarDiaryTypography.DiaryBody,
                    modifier = Modifier.width(900.dp),
                )
            }

            Section("Icon (Feather, 32px)") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CarDiaryIcons.all.forEach { (name, icon) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CarDiaryIcon(icon, contentDescription = name)
                            CarDiaryText(name, style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub)
                        }
                    }
                }
            }

            Section("Icon Button · Button · Text Button") {
                CarDiaryIconButton(CarDiaryIcons.ArrowLeft, contentDescription = "뒤로 가기", onClick = {})
                ButtonStyle.entries.forEach { style ->
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        CarDiaryButton("들려주기", onClick = {}, style = style, icon = CarDiaryIcons.Play)
                        CarDiaryButton("들려주기", onClick = {}, style = style, icon = CarDiaryIcons.Pause, forcePressed = true)
                        CarDiaryButton("들려주기", onClick = {}, style = style, icon = CarDiaryIcons.Pause, enabled = false)
                        CarDiaryText(style.name, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryButton("폰으로 보내기", onClick = {}, style = ButtonStyle.Secondary, icon = CarDiaryIcons.Send)
                    CarDiaryButton("기록 끄기 (오늘만)", onClick = {}, style = ButtonStyle.Secondary)
                }
                CarDiaryTextButton("전체보기", onClick = {})
            }

            Section("Tag · Piece Tag · Status Pill · Tab Chip") {
                CarDiaryTag("출발")
                Row(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap)) {
                    CarDiaryPieceTag("벚꽃 흩날리며")
                    CarDiaryPieceTag("+3", style = PieceTagStyle.More)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryStatusPill("오늘은 기록 안 함", tone = StatusTone.Neutral)
                    CarDiaryStatusPill("기록 중", tone = StatusTone.Accent, blinking = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap)) {
                    listOf("11월", "10월", "9월").forEachIndexed { index, month ->
                        CarDiaryTabChip(month, selected = index == selectedMonth, onClick = { selectedMonth = index })
                    }
                }
            }

            Section("Section Header · Highlight Row · Route Point · Avatar") {
                CarDiarySectionHeader("오늘의 명장면", count = 3, onMore = {}, modifier = Modifier.width(561.dp))
                CarDiaryHighlightRow("10:42", "비가 오기 시작했어", emoji = "💧")
                CarDiaryHighlightRow("11:05", "쭉 뻗은 길 40분", emoji = "🛣️")
                CarDiaryRoute(
                    from = { CarDiaryRoutePoint("우리 집", "10:40", "출발") },
                    to = { CarDiaryRoutePoint("할머니 댁", "12:20", "도착") },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    CarDiaryAvatar()
                    CarDiaryAvatar(size = com.hyunsikyoo.chagokchagok.ui.designsystem.component.AvatarSize.Small)
                    CarDiaryAvatarStack(count = 3)
                    CarDiaryText("아빠, 엄마, 준", style = CarDiaryTypography.TitleSmallStrong)
                }
            }

            Section("Card · Banner · Progress Bar") {
                Row(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ColumnGap)) {
                    CardShadow.entries.forEach { shadow ->
                        CarDiaryCard(Modifier.width(CarDiaryDimens.ColumnSmallWide), shadow = shadow) {
                            CarDiarySectionHeader("오늘의 명장면", count = 3, onMore = {})
                            CarDiaryPlaceholder("내용 자리", Modifier.fillMaxWidth().height(140.dp))
                        }
                    }
                }
                CarDiaryBanner(caption = "1년 전 오늘", title = "우리 강릉 바다에 갔었어", onClick = {}, modifier = Modifier.width(564.dp))
                CarDiaryProgressBar(progress = 3214f / 5000f, contentDescription = "5,000km까지 64%")
            }

            Section("Toggle · List Row") {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryToggle(toggleA, { toggleA = it })
                    CarDiaryToggle(toggleB, { toggleB = it })
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryListRow("가족과 자리", subtitle = "아빠 · 엄마 · 준", modifier = Modifier.width(592.dp), onClick = {})
                    CarDiaryListRow("가족과 자리", subtitle = "아빠 · 엄마 · 준", selected = true, modifier = Modifier.width(592.dp), onClick = {})
                    CarDiaryListRow(
                        "특별한 날 알림", subtitle = "1년 전 오늘, 연말 결산", modifier = Modifier.width(592.dp),
                        trailing = ListRowTrailing.Toggle(toggleB) { toggleB = it },
                    )
                    CarDiaryListRow(
                        "오늘은 기록 안 하기", subtitle = "꺼짐 · 내일 다시 켜져요", selected = true, modifier = Modifier.width(592.dp),
                        trailing = ListRowTrailing.Toggle(toggleA) { toggleA = it },
                    )
                }
            }

            Section("Seat Card · Calendar Day · Milestone Card") {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiarySeatCard("운전석", "아빠", filled = true, onClick = {})
                    CarDiarySeatCard("뒷자리 오른쪽", "빈자리", filled = false, onClick = {})
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryCalendarDay(1, CalendarDayState.Empty)
                    CarDiaryCalendarDay(2, CalendarDayState.Empty, today = true)
                    CarDiaryCalendarDay(3, CalendarDayState.Diary, emoji = FluentEmoji.Star.painter())
                    CarDiaryCalendarDay(4, CalendarDayState.Selected, emoji = FluentEmoji.Droplet.painter())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CarDiaryMilestoneCard("3,000km", "서울↔부산 4번\n10월 19일", done = true)
                    CarDiaryMilestoneCard("10,000km", "타임캡슐 편지", done = false)
                }
            }

            Section("Popup") {
                CarDiaryButton("팝업 열기", onClick = { showPopup = true }, style = ButtonStyle.Neutral)
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CarDiaryText(title, style = CarDiaryTypography.TitleSmallStrong, color = CarDiaryColors.TextCaption)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CarDiaryColors.Card, CarDiaryShapes.Card)
                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
                .padding(CarDiaryDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) { content() }
    }
}

@Composable
private fun Swatch(name: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .size(width = 120.dp, height = 64.dp)
                .background(color, CarDiaryShapes.ListRow)
                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow),
        )
        CarDiaryText(name, style = CarDiaryTypography.LabelSmall)
    }
}
