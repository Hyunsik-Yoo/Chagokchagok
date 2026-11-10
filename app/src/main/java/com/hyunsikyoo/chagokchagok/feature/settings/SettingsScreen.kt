package com.hyunsikyoo.chagokchagok.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.data.demo.DemoDiaryRepository
import com.hyunsikyoo.chagokchagok.domain.model.FamilyMember
import com.hyunsikyoo.chagokchagok.domain.model.Relation
import com.hyunsikyoo.chagokchagok.domain.model.Seat
import com.hyunsikyoo.chagokchagok.feature.common.FamilyForm
import com.hyunsikyoo.chagokchagok.feature.common.SubText
import com.hyunsikyoo.chagokchagok.feature.common.rememberFamilyFormState
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonSize
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ButtonStyle
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryAppFrame
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryButton
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryListRow
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySeatCard
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiarySectionHeader
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryTextField
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.HeaderNav
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.ListRowTrailing
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons
import com.hyunsikyoo.chagokchagok.ui.navigation.SettingsState
import java.time.LocalDate

/** 오른쪽 상세 */
private sealed interface Detail {
    data object CarName : Detail
    data object FamilySeats : Detail
    data object AddMember : Detail
    data class EditMember(val member: FamilyMember) : Detail
}

/**
 * 설정 (W9, 디자인 완료 · 노드 92:6116 / W9-1 109:8178 / W9-2 113:10757).
 * 왼쪽 목록 4줄(차 이름, 가족과 자리, 특별한 날 알림, 오늘은 기록 안 하기) + 오른쪽 상세, 화면 이동 없이 처리.
 */
@Composable
fun SettingsScreen(
    settings: SettingsState,
    onBack: () -> Unit,
    onCarName: (String) -> Unit,
    onSpecialDay: (Boolean) -> Unit,
    onRecordingOff: (Boolean) -> Unit,
    onAddMember: (String, Relation, LocalDate?) -> FamilyMember,
    onUpdateMember: (FamilyMember) -> Unit,
    onDeleteMember: (FamilyMember) -> Unit,
) {
    var detail by remember { mutableStateOf<Detail>(Detail.FamilySeats) }
    val today = DemoDiaryRepository.today
    CarDiaryAppFrame(header = { CarDiaryHeader(title = "설정", nav = HeaderNav.BackOnly, onBack = onBack) }) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ColumnGap)) {
            // 2 설정 목록 4줄
            Column(Modifier.width(560.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CarDiaryListRow("차 이름", subtitle = settings.carName, selected = detail == Detail.CarName, onClick = { detail = Detail.CarName }, modifier = Modifier.fillMaxWidth())
                CarDiaryListRow(
                    "가족과 자리",
                    subtitle = settings.family.joinToString(", ") { it.name },
                    selected = detail != Detail.CarName,
                    onClick = { detail = Detail.FamilySeats },
                    modifier = Modifier.fillMaxWidth(),
                )
                CarDiaryListRow(
                    "특별한 날 알림", subtitle = "1년 전 오늘, 연말 결산",
                    trailing = ListRowTrailing.Toggle(settings.specialDayAlerts, onSpecialDay),
                    modifier = Modifier.fillMaxWidth(),
                )
                CarDiaryListRow(
                    "오늘은 기록 안 하기", subtitle = "켜 두면 내일 저절로 꺼져요",
                    trailing = ListRowTrailing.Toggle(settings.recordingOffToday, onRecordingOff),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // 오른쪽 상세 (Card Hard)
            Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())) {
                when (val d = detail) {
                    Detail.CarName -> CarNameDetail(settings.carName, settings.mainChild?.name ?: "준", onCarName)
                    Detail.FamilySeats -> FamilySeatsDetail(
                        settings, today,
                        onEdit = { detail = Detail.EditMember(it) },
                        onAdd = { detail = Detail.AddMember },
                    )
                    Detail.AddMember -> MemberDetail(
                        title = "새 가족을 알려 줘",
                        subtitle = "이름, 관계, 생일을 한 번만 넣어요",
                        member = null,
                        today = today,
                        onSave = { name, relation, birthday -> onAddMember(name, relation, birthday); detail = Detail.FamilySeats },
                        onDelete = null,
                    )
                    is Detail.EditMember -> MemberDetail(
                        title = "${d.member.name}의 정보 고치기",
                        subtitle = "가족과 자리에서 자리 카드의 사람을 누르면 열려요",
                        member = d.member,
                        today = today,
                        onSave = { name, relation, birthday -> onUpdateMember(d.member.copy(name = name.trim(), relation = relation, birthday = birthday)); detail = Detail.FamilySeats },
                        onDelete = { onDeleteMember(d.member); detail = Detail.FamilySeats },
                    )
                }
            }
        }
    }
}

/** W9-1 차 이름: Text Field + 연필, 미리보기(노란 상자), 저장 Small Primary. 저장하면 앞으로 쓰는 일기부터 새 이름 */
@Composable
private fun CarNameDetail(current: String, childName: String, onSave: (String) -> Unit) {
    var name by remember(current) { mutableStateOf(current) }
    val preview = name.ifBlank { current }
    CarDiaryCard(Modifier.fillMaxWidth(), shadow = CardShadow.Hard) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CarDiarySectionHeader("차 이름")
            SubText("${current}가 일기에서 자기를 부르는 이름이에요. 우리 차에 붙인 별명을 넣어 주세요")
        }
        CarDiaryTextField(label = "이름", value = name, onValueChange = { if (it.length <= 8) name = it }, icon = CarDiaryIcons.Edit2)
        Column(
            Modifier
                .fillMaxWidth()
                .background(CarDiaryColors.YellowBackground, CarDiaryShapes.ListRow)
                .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow)
                .padding(horizontal = 26.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SubText("이렇게 쓰여요")
            CarDiaryText("\"오늘은 ${preview}가 ${childName}이랑 할머니 댁에 다녀왔어\"", style = CarDiaryTypography.BodyMediumStrong)
            CarDiaryText("\"${preview}의 일기장\", \"${preview}의 한마디\"처럼 화면 곳곳에도 이 이름이 들어가요", style = CarDiaryTypography.LabelSmall, color = CarDiaryColors.TextSub)
        }
        CarDiaryButton(
            "저장",
            onClick = { onSave(name) },
            style = ButtonStyle.Primary,
            size = ButtonSize.Small,
            enabled = name.isNotBlank() && name.trim() != current,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

/** 가족과 자리: Seat Card 4칸(여기서 바꾼 값이 탑승 팝업 기본값) + [가족 추가]. 자리 카드의 사람을 누르면 W9-2 */
@Composable
private fun FamilySeatsDetail(
    settings: SettingsState,
    today: LocalDate,
    onEdit: (FamilyMember) -> Unit,
    onAdd: () -> Unit,
) {
    CarDiaryCard(Modifier.fillMaxWidth(), shadow = CardShadow.Hard) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CarDiarySectionHeader("가족과 자리")
            SubText("한 번 맞춰 두면 탈 때마다 \"오늘 누구랑 가?\"에 미리 채워져요")
        }
        SeatGrid(
            seats = settings.seats,
            today = today,
            emptyName = "비어 있음",
            onSeatClick = { seat -> settings.seats[seat]?.let(onEdit) ?: onAdd() },
        )
        CarDiaryButton("가족 추가", onClick = onAdd, style = ButtonStyle.Neutral, size = ButtonSize.Small, icon = CarDiaryIcons.Plus, modifier = Modifier.align(Alignment.End))
    }
}

/** W3-2·W9 가족 추가·W9-2 가족 고치기: 이름, 관계, 생일 + 나이 */
@Composable
private fun MemberDetail(
    title: String,
    subtitle: String,
    member: FamilyMember?,
    today: LocalDate,
    onSave: (String, Relation, LocalDate?) -> Unit,
    onDelete: (() -> Unit)?,
) {
    val form = rememberFamilyFormState(member)
    CarDiaryCard(Modifier.fillMaxWidth(), shadow = CardShadow.Hard) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CarDiarySectionHeader(title)
            SubText(subtitle)
        }
        FamilyForm(form, today, ageHint = if (onDelete != null) "지우면 이 사람의 자리만 비고 지난 일기는 남아요" else "나이는 생일로 계산해서 일기에 써요")
        Row(Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ButtonGap)) {
            if (onDelete != null) {
                CarDiaryButton("이 가족 지우기", onClick = onDelete, style = ButtonStyle.Neutral, size = ButtonSize.Small, icon = CarDiaryIcons.Trash2)
            }
            CarDiaryButton(
                "저장",
                onClick = { form.relation?.let { onSave(form.name, it, form.birthday) } },
                style = ButtonStyle.Primary,
                size = ButtonSize.Small,
                enabled = form.valid,
            )
        }
    }
}

/** 차 모양 그대로 2×2 자리 칸 (탑승 팝업과 같이 쓴다). 아이는 이름 옆 나이 태그 */
@Composable
fun SeatGrid(
    seats: Map<Seat, FamilyMember?>,
    today: LocalDate,
    onSeatClick: (Seat) -> Unit,
    modifier: Modifier = Modifier,
    emptyName: String = "빈자리",
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        listOf(Seat.Driver to Seat.Passenger, Seat.RearLeft to Seat.RearRight).forEach { (left, right) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                listOf(left, right).forEach { seat ->
                    val member = seats[seat]
                    CarDiarySeatCard(
                        seat = seat.label,
                        name = member?.name ?: emptyName,
                        filled = member != null,
                        ageLabel = member?.takeIf { it.isChild }?.ageOn(today)?.let { "${it}살" },
                        onClick = { onSeatClick(seat) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
