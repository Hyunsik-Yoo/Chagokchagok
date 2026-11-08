package com.hyunsikyoo.pleosapp.feature.popups

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.hyunsikyoo.pleosapp.data.demo.DemoDiaryRepository
import com.hyunsikyoo.pleosapp.data.tts.NarrationState
import com.hyunsikyoo.pleosapp.domain.DiaryFormat
import com.hyunsikyoo.pleosapp.domain.model.Diary
import com.hyunsikyoo.pleosapp.domain.model.FamilyMember
import com.hyunsikyoo.pleosapp.domain.model.Relation
import com.hyunsikyoo.pleosapp.domain.model.Seat
import com.hyunsikyoo.pleosapp.feature.common.FamilyForm
import com.hyunsikyoo.pleosapp.feature.common.Paperclip
import com.hyunsikyoo.pleosapp.feature.common.SubText
import com.hyunsikyoo.pleosapp.feature.common.rememberFamilyFormState
import com.hyunsikyoo.pleosapp.feature.settings.SeatGrid
import com.hyunsikyoo.pleosapp.ui.designsystem.component.ButtonSize
import com.hyunsikyoo.pleosapp.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryHighlightRow
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryIcon
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryListRow
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryPopup
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryPopupScrim
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryStatusPill
import com.hyunsikyoo.pleosapp.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.pleosapp.ui.designsystem.component.ListRowTrailing
import com.hyunsikyoo.pleosapp.ui.designsystem.component.PopupAction
import com.hyunsikyoo.pleosapp.ui.designsystem.component.StatusTone
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.painter
import kotlinx.coroutines.delay
import java.time.LocalDate

// ───────────────────────── W3 탑승 ─────────────────────────

/** 탑승 팝업 안 단계 (같은 팝업 안에서 바뀐다) */
private sealed interface BoardingStep {
    data object Seats : BoardingStep
    data class Pick(val seat: Seat) : BoardingStep
    data class Add(val seat: Seat) : BoardingStep
}

/**
 * 탑승 (W3, 디자인 완료 · 노드 93:12024). 뒷문 열림 + 기어 P, 또는 홈 [기록 시작]에서 뜬다.
 * - W3-0(105:6850): 저장된 자리가 하나도 없으면 "처음이라 자리가 비어 있어요", 칸 이름 "누가 앉았어?"
 * - W3-1(105:7385): 칸을 누르면 "○○에 누가 앉았어?" 가족 목록(체크박스 하나만) + 새 가족 혹은 친구 추가 + [이 자리에 앉히기]
 * - W3-2(107:7583): 이름·관계·생일 → [저장하기] = 가족 목록에 추가하고 처음 누른 자리에 바로 앉힌 뒤 W3로
 * 기어 D가 되면 고른 대로 확정하고 닫힌다(입력 중이던 W3-2 내용은 버린다).
 */
@Composable
fun BoardingPopup(
    seats: Map<Seat, FamilyMember?>,
    family: List<FamilyMember>,
    today: LocalDate,
    onSetSeat: (Seat, FamilyMember?) -> Unit,
    onAddMember: (String, Relation, LocalDate?) -> FamilyMember,
    onConfirm: () -> Unit,
    onSkipToday: () -> Unit,
) {
    val firstTime = remember { seats.values.all { it == null } }
    var step by remember { mutableStateOf<BoardingStep>(BoardingStep.Seats) }
    CarDiaryPopupScrim {
        when (val s = step) {
            BoardingStep.Seats -> CarDiaryPopup(
                title = "오늘 누구랑 가?",
                subtitle = if (firstTime) "처음이라 자리가 비어 있어요. 자리를 눌러서 누가 앉았는지 알려 줘" else "자리에 맞춰 미리 골라 뒀어요. 다르면 눌러서 바꿔요",
                footnote = if (firstTime) "한 번 고르면 다음 탑승부터 기본으로 들어가요" else "기어를 D에 넣으면 이 창은 저절로 닫힙니다",
                actions = listOf(
                    PopupAction("오늘은 기록 안 해", onSkipToday, ButtonStyle.Neutral),
                    PopupAction("이대로 출발", onConfirm, ButtonStyle.Primary),
                ),
            ) {
                SeatGrid(
                    seats = seats,
                    today = today,
                    emptyName = if (firstTime) "누가 앉았어?" else "빈자리",
                    onSeatClick = { step = BoardingStep.Pick(it) },
                )
            }
            is BoardingStep.Pick -> {
                var chosen by remember(s.seat) { mutableStateOf(seats[s.seat]?.id) }
                CarDiaryPopup(
                    title = "${s.seat.label}에 누가 앉았어?",
                    subtitle = "가족 중에서 골라 줘",
                    onBack = { step = BoardingStep.Seats },
                    footnote = "나이는 생일로 계산해서 일기에 써요",
                    actions = listOf(
                        PopupAction("이 자리에 앉히기", {
                            onSetSeat(s.seat, family.firstOrNull { it.id == chosen })
                            step = BoardingStep.Seats
                        }, ButtonStyle.Primary),
                    ),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        family.forEach { m ->
                            CarDiaryListRow(
                                title = m.name,
                                subtitle = memberLine(m, today),
                                selected = chosen == m.id,
                                trailing = ListRowTrailing.Checkbox,
                                onClick = { chosen = if (chosen == m.id) null else m.id },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        CarDiaryListRow(
                            title = "새 가족 혹은 친구 추가",
                            subtitle = "이름, 관계, 생일을 한 번만 넣어요",
                            trailing = ListRowTrailing.Icon(CarDiaryIcons.Plus),
                            onClick = { step = BoardingStep.Add(s.seat) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            is BoardingStep.Add -> {
                val form = rememberFamilyFormState()
                CarDiaryPopup(
                    title = "새 가족을 알려 줘",
                    subtitle = "한 번만 넣으면 다음부터는 고르기만 하면 돼요",
                    onBack = { step = BoardingStep.Pick(s.seat) },
                    footnote = "차에서 글자를 넣는 건 처음 한 번뿐이에요",
                    actions = listOf(
                        PopupAction("저장하기", {
                            form.relation?.let { r ->
                                val member = onAddMember(form.name, r, form.birthday)
                                onSetSeat(s.seat, member)
                                step = BoardingStep.Seats
                            }
                        }, ButtonStyle.Primary, enabled = form.valid),
                    ),
                ) {
                    FamilyForm(form, today, ageHint = "나이는 생일로 계산해서 일기에 써요")
                }
            }
        }
    }
}

/** "아들, 6살 (생일 5월 3일)" */
private fun memberLine(m: FamilyMember, today: LocalDate): String {
    val parts = mutableListOf<String>()
    if (m.relation != Relation.Dad && m.relation != Relation.Mom) parts += m.relation.label
    m.ageOn(today)?.let { parts += "${it}살" }
    val birthday = m.birthday?.let { " (생일 ${it.monthValue}월 ${it.dayOfMonth}일)" }.orEmpty()
    return parts.joinToString(", ") + birthday
}

// ───────────────────────── W6 도착 ─────────────────────────

private data class PlaceCandidate(val name: String, val desc: String)

private sealed interface ArrivalStep {
    data object List : ArrivalStep
    data object Listening : ArrivalStep
    data class Heard(val name: String?) : ArrivalStep
}

/**
 * 도착: 여기 어디였어? (W6, 디자인 완료 · 노드 89:5486). 트립이 끝나면 장소 이름을 확정한다.
 * - 내비 목적지 줄이 기본 체크(List Row Checkbox). 다른 줄을 체크하면 앞의 체크가 풀린다.
 * - W6-1(111:8715): 내비 목적지가 없으면 후보 3개 체크 없음, 주 버튼 Disabled, 30초 뒤 장소 없이 일기(제목은 동네 이름).
 * - 이름 붙이기 줄(노랑, 마이크) → W6-2(102:6589) 음성으로 듣기 → 못 알아들으면 W6-3(111:9078). 폰으로 이름 붙이기는 없다.
 * Gleo 음성 인식 연결 전이라 시연용으로 2초 뒤 "공룡 공원"을 받아 적는다.
 */
@Composable
fun ArrivalPopup(
    destination: String?,
    childName: String,
    secondsLeft: Int,
    onConfirm: (String?) -> Unit,
    onLater: () -> Unit,
    onPauseCountdown: (Boolean) -> Unit,
) {
    val noDestination = destination == null
    val candidates = remember(destination) {
        mutableListOf<PlaceCandidate>().apply {
            if (destination != null) add(PlaceCandidate(destination, "경기 양평군 ○○로 12 (내비 목적지)"))
            add(PlaceCandidate("두물머리", "근처 장소"))
            if (noDestination) add(PlaceCandidate("양평 카페거리", "근처 장소"))
            add(PlaceCandidate("양평 시장", "지난번에 간 곳"))
        }
    }
    var named by remember { mutableStateOf<String?>(null) }
    var chosen by remember { mutableStateOf(if (noDestination) null else candidates.first().name) }
    var step by remember { mutableStateOf<ArrivalStep>(ArrivalStep.List) }
    var attempt by remember { mutableIntStateOf(0) }

    DisposableEffect(step) {
        onPauseCountdown(step != ArrivalStep.List)
        onDispose { }
    }
    LaunchedEffect(step, attempt) {
        if (step == ArrivalStep.Listening) {
            delay(2_000) // 시연: 음성 인식 결과
            step = ArrivalStep.Heard("공룡 공원")
        }
    }

    CarDiaryPopupScrim {
        when (val s = step) {
            ArrivalStep.List -> CarDiaryPopup(
                title = "여기 어디였어?",
                subtitle = if (noDestination) "내비 목적지가 없었어요. 어디였는지 골라 줘" else "내비 목적지로 찾았어요. 맞으면 그대로 두면 돼요",
                footnote = if (noDestination) "${secondsLeft}초 동안 아무것도 안 고르면 동네 이름으로 일기를 써요" else "${secondsLeft}초 동안 아무것도 안 누르면 내비 목적지로 저장돼요",
                actions = listOf(
                    PopupAction("나중에", onLater, ButtonStyle.Neutral),
                    PopupAction("맞아, 여기야", { onConfirm(chosen) }, ButtonStyle.Primary, enabled = chosen != null),
                ),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StaticMap(Modifier.width(500.dp).height(500.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val rows = listOfNotNull(named?.let { PlaceCandidate(it, "${childName}이가 붙인 이름") }) + candidates
                        rows.forEachIndexed { i, c ->
                            if (!noDestination && i == (if (named != null) 2 else 1)) SubText("다른 곳이면 골라 주세요")
                            CarDiaryListRow(
                                title = c.name,
                                subtitle = c.desc,
                                selected = chosen == c.name,
                                trailing = ListRowTrailing.Checkbox,
                                onClick = { chosen = c.name },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        CarDiaryListRow(
                            title = "${childName}이가 이름 붙이기",
                            subtitle = "음성으로 이름을 넣어주세요",
                            trailing = ListRowTrailing.Icon(CarDiaryIcons.Mic),
                            background = CarDiaryColors.YellowBackground,
                            onClick = { step = ArrivalStep.Listening; attempt++ },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            ArrivalStep.Listening, is ArrivalStep.Heard -> {
                val heard = (s as? ArrivalStep.Heard)?.name
                val failed = s is ArrivalStep.Heard && heard.isNullOrBlank()
                CarDiaryPopup(
                    title = "${childName}아, 여기를 뭐라고 부를까?",
                    subtitle = "말하면 소나가 받아 적을게",
                    onBack = { step = ArrivalStep.List },
                    actions = listOf(
                        PopupAction("다시 말하기", { step = ArrivalStep.Listening; attempt++ }, if (failed) ButtonStyle.Primary else ButtonStyle.Neutral, CarDiaryIcons.Mic),
                        PopupAction("이 이름으로 저장", {
                            named = heard
                            chosen = heard
                            step = ArrivalStep.List
                        }, ButtonStyle.Primary, enabled = !heard.isNullOrBlank()),
                    ),
                ) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(28.dp)) {
                        CarDiaryStatusPill(if (failed) "못 알아들었어" else "듣는 중", tone = if (failed) StatusTone.Neutral else StatusTone.Accent, blinking = s == ArrivalStep.Listening)
                        Box(
                            Modifier
                                .size(156.dp)
                                .hardShadow(CarDiaryShadows.Card, CircleShape)
                                .background(CarDiaryColors.Main, CircleShape)
                                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CircleShape)
                                .clickable(role = Role.Button, onClickLabel = "다시 듣기") { step = ArrivalStep.Listening; attempt++ },
                            contentAlignment = Alignment.Center,
                        ) { CarDiaryIcon(CarDiaryIcons.Mic, contentDescription = null, size = 64.dp) }
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(if (failed) CarDiaryColors.Surface else CarDiaryColors.YellowBackground, CarDiaryShapes.ListRow)
                                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow)
                                .padding(vertical = 26.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CarDiaryText(
                                when {
                                    failed -> "잘 못 들었어. 한 번 더 말해 줄래?"
                                    heard != null -> "\"$heard\""
                                    else -> "듣고 있어…"
                                },
                                style = CarDiaryTypography.TitleLargeStrong,
                                textAlign = TextAlign.Center,
                            )
                            SubText(if (failed) "천천히, 또박또박 말해 주면 돼" else "다음에 여기 오면 이 이름으로 일기를 써요")
                        }
                    }
                }
            }
        }
    }
}

/** 정지 지도 자리: 확대·이동 없는 확인용 그림 + 현재 위치 핀 하나 (지도 SDK 연결 전) */
@Composable
private fun StaticMap(modifier: Modifier) {
    Box(
        modifier
            .background(CarDiaryColors.SkyBackground, CarDiaryShapes.ListRow)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow)
            .semantics { contentDescription = "현재 위치 정지 지도" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize().padding(2.dp)) {
            val road = Color.White
            drawLine(road, Offset(0f, size.height * 0.38f), Offset(size.width, size.height * 0.46f), 28f)
            drawLine(road, Offset(size.width * 0.58f, 0f), Offset(size.width * 0.42f, size.height), 22f)
            drawLine(Color(0xFFBFD9F2), Offset(0f, size.height * 0.82f), Offset(size.width, size.height * 0.72f), 40f)
            drawRect(Color(0x1A000000), Offset(size.width * 0.1f, size.height * 0.1f), Size(size.width * 0.28f, size.height * 0.16f))
        }
        CarDiaryIcon(CarDiaryIcons.MapPin, contentDescription = null, size = 64.dp, modifier = Modifier.offset(y = (-24).dp))
    }
}

// ───────────────────────── W7 폰으로 보내기 ─────────────────────────

/**
 * 폰으로 보내기 (W7, 디자인 완료 · 노드 90:5442). QR 420px(일기 1편 링크), 세 단계 안내, 노란 예시 상자, 개인정보 한 줄, [닫기].
 * W7-1(113:10322): 5분이 지나면 QR 자리를 #000 5%로 바꾸고 "5분이 지나 링크가 닫혔어요" + [다시 만들기] Small Secondary.
 */
@Composable
fun SendPhonePopup(diary: Diary?, carName: String, childName: String, onClose: () -> Unit) {
    var issued by remember { mutableIntStateOf(0) }
    var secondsLeft by remember(issued) { mutableIntStateOf(5 * 60) }
    LaunchedEffect(issued) {
        while (secondsLeft > 0) {
            delay(1_000)
            secondsLeft--
        }
    }
    val link = "chailgi://diary/${diary?.date ?: DemoDiaryRepository.latest().date}?t=demo$issued"
    CarDiaryPopupScrim(onDismiss = onClose) {
        CarDiaryPopup(
            title = "폰으로 보내기",
            subtitle = "오늘 일기를 폰에서 이어서 볼 수 있어요",
            actions = listOf(PopupAction("닫기", onClose, ButtonStyle.Neutral)),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(420.dp)
                        .background(if (secondsLeft > 0) CarDiaryColors.Card else CarDiaryColors.Surface, CarDiaryShapes.Card)
                        .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card),
                    contentAlignment = Alignment.Center,
                ) {
                    if (secondsLeft > 0) {
                        QrCode(link, Modifier.size(340.dp))
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            CarDiaryText("5분이 지나 링크가 닫혔어요", style = CarDiaryTypography.BodySmallStrong)
                            CarDiaryButton("다시 만들기", onClick = { issued++ }, style = ButtonStyle.Secondary, size = ButtonSize.Small)
                        }
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Step(1, "폰 카메라로 QR을 찍어요")
                    Step(2, "오늘 일기가 폰에 저장돼요")
                    Step(3, "폰에서 사진이랑 한마디를 덧붙일 수 있어요")
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(CarDiaryColors.YellowBackground, CarDiaryShapes.ListRow)
                            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow)
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        CarDiaryText("예) \"${childName}이가 휴게소에서 소떡소떡 두 개 먹음\"", style = CarDiaryTypography.BodyMediumNormal)
                        CarDiaryText("덧붙인 내용은 다음에 탈 때 ${carName}가 함께 읽어 줘요", style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
                    }
                    CarDiaryText("위치와 가족 이름은 폰으로 보낼 때만 폰에서 확인이 가능해요", style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub)
                }
            }
        }
    }
}

@Composable
private fun Step(number: Int, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            Modifier.size(52.dp).background(CarDiaryColors.SkyBackground, CircleShape).border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CircleShape),
            contentAlignment = Alignment.Center,
        ) { CarDiaryText(number.toString(), style = CarDiaryTypography.BodyLargeStrong) }
        CarDiaryText(title, style = CarDiaryTypography.BodyLargeStrong)
    }
}

/** zxing으로 QR 행렬을 만들어 그대로 칠한다 */
@Composable
private fun QrCode(text: String, modifier: Modifier) {
    val matrix = remember(text) { QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 0, 0) }
    Canvas(modifier.semantics { contentDescription = "일기 링크 QR 코드" }) {
        val cells = matrix.width
        val cell = size.minDimension / cells
        for (y in 0 until matrix.height) for (x in 0 until cells) {
            if (matrix.get(x, y)) drawRect(Color.Black, Offset(x * cell, y * cell), Size(cell + 0.5f, cell + 0.5f))
        }
    }
}

// ───────────────────────── W8 특별한 날 ─────────────────────────

/**
 * 특별한 날: 1년 전 오늘 (W8, 디자인 완료 · 노드 91:5768). 정차 중 홈을 열 때 하루 한 번.
 * 종류 배지 · 소나의 한마디(부제에 아이 나이) · 그날 일기(제목 + 앞 4줄, 종이 줄, 클립) · 그날 요약(명장면 3줄 + 그때와 지금)
 * · [닫기] [들려주기] [일기 보기]. 타임캡슐은 범위 제외.
 */
@Composable
fun SpecialDayPopup(
    childName: String,
    narration: NarrationState,
    onPlay: () -> Unit,
    onOpenDiary: () -> Unit,
    onClose: () -> Unit,
) {
    val diary = DemoDiaryRepository.yearAgo
    val child = DemoDiaryRepository.jun
    val ageThen = child.ageOn(diary.date)
    val playing = narration.playing(diary.date.toString())
    val place = diary.trips.last().endPlace?.name.orEmpty()
    CarDiaryPopupScrim(onDismiss = onClose) {
        CarDiaryPopup(
            title = "작년 오늘, 우리 ${place}에 다녀왔어",
            subtitle = "${childName}이는 그때 ${ageThen?.let(::koreanAge) ?: "더 어렸"}${if (ageThen != null) " 살이었지" else "지"}. 비가 와서 와이퍼가 바빴던 거 기억나?",
            badge = {
                Box(
                    Modifier
                        .background(CarDiaryColors.YellowBackground, CarDiaryShapes.Pill)
                        .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                ) { CarDiaryText("1년 전 오늘", style = CarDiaryTypography.BodySmallNormal) }
            },
            actions = listOf(
                PopupAction("닫기", onClose, ButtonStyle.Neutral),
                PopupAction(if (playing) narration.elapsedLabel else "들려주기", onPlay, ButtonStyle.Primary, if (playing) CarDiaryIcons.Pause else CarDiaryIcons.Play),
                PopupAction("일기 보기", onOpenDiary, ButtonStyle.Secondary),
            ),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                Column(Modifier.width(480.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .hardShadow(CarDiaryShadows.Card, CarDiaryShapes.Card)
                                .background(CarDiaryColors.Card, CarDiaryShapes.Card)
                                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Card)
                                .padding(horizontal = 30.dp, vertical = 26.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CarDiaryText(diary.title, style = CarDiaryTypography.BodyLargeStrong)
                            diary.sentences.take(4).forEach { line ->
                                CarDiaryText(line, style = CarDiaryTypography.BodySmallNormal, maxLines = 1)
                                Box(Modifier.fillMaxWidth().height(CarDiaryDimens.Stroke).background(CarDiaryColors.LineLight))
                            }
                        }
                        Paperclip(Modifier.align(Alignment.TopEnd).offset(x = (-16).dp, y = (-30).dp))
                    }
                    SubText("${DiaryFormat.date(diary.date)}  |  ${diary.trips.first().startPlace.name} → $place")
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    CarDiaryText("그날의 명장면", style = CarDiaryTypography.BodyLargeStrong)
                    diary.records.forEach { h ->
                        CarDiaryHighlightRow(DiaryFormat.time(h.at), h.title, h.type.emoji.char, icon = h.type.emoji.painter())
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(CarDiaryColors.YellowBackground, CarDiaryShapes.ListRow)
                            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow)
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(48.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SubText("그때 함께 달린 길")
                            CarDiaryText(DiaryFormat.distance(DemoDiaryRepository.YEAR_AGO_MILEAGE_KM), style = CarDiaryTypography.TitleMediumStrong)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SubText("지금은")
                            CarDiaryText(DiaryFormat.distance(DemoDiaryRepository.mileage().totalKm), style = CarDiaryTypography.TitleMediumStrong)
                        }
                    }
                }
            }
        }
    }
}

/** 아이 나이를 말로: 5 → "다섯" */
private fun koreanAge(age: Int): String =
    listOf("", "한", "두", "세", "네", "다섯", "여섯", "일곱", "여덟", "아홉", "열").getOrElse(age) { age.toString() }
