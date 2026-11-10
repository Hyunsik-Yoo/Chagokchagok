package com.hyunsikyoo.chagokchagok.feature.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.chagokchagok.domain.model.FamilyMember
import com.hyunsikyoo.chagokchagok.domain.model.FluentEmoji
import com.hyunsikyoo.chagokchagok.domain.model.Relation
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryTabChip
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryText
import com.hyunsikyoo.chagokchagok.ui.designsystem.component.CarDiaryTextField
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.painter
import java.time.LocalDate

/** 명장면 아이콘 줄 */
@Composable
fun EmojiRow(emojis: List<FluentEmoji>, modifier: Modifier = Modifier, size: Dp = 56.dp) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        emojis.forEach { Image(it.painter(), contentDescription = it.char, modifier = Modifier.size(size)) }
    }
}

/** 보조 글 (24 Regular, 검정 50%) */
@Composable
fun SubText(text: String, modifier: Modifier = Modifier) {
    CarDiaryText(text, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub, modifier = modifier)
}

/** 날씨 글 → 날씨 이모지 (홈 지난 일기 카드, 일기 요약) */
fun weatherEmoji(label: String?): FluentEmoji = when {
    label == null -> FluentEmoji.Sun
    "비" in label && "갬" !in label -> FluentEmoji.Droplet
    "눈" in label -> FluentEmoji.Snowflake
    "흐림" in label || "갬" in label -> FluentEmoji.SunBehindCloud
    else -> FluentEmoji.SunBehindCloud
}

/** 일기 카드 위 클립 장식 (홈 가장 최근 일기, 특별한 날 그날 일기) */
@Composable
fun Paperclip(modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = 64.dp, height = 72.dp)) {
        val stroke = Stroke(width = 4.dp.toPx())
        val color = CarDiaryColors.Line
        rotate(degrees = 38f) {
            val w = 22.dp.toPx()
            val h = 64.dp.toPx()
            val left = size.width / 2 - w / 2
            val top = (size.height - h) / 2
            drawRoundRect(color, Offset(left, top), Size(w, h), CornerRadius(w / 2), style = stroke)
            val inset = 6.dp.toPx()
            drawRoundRect(color, Offset(left + inset, top + inset * 2), Size(w - inset * 2, h - inset * 5), CornerRadius((w - inset * 2) / 2), style = stroke)
        }
    }
}

/** 생일 글 → 날짜. "2021년 8월 12일", "2021-08-12", "20210812" 모두 받는다 */
fun parseBirthday(text: String): LocalDate? {
    val digits = Regex("\\d+").findAll(text).map { it.value }.toList()
    return runCatching {
        when {
            digits.size >= 3 -> LocalDate.of(digits[0].toInt(), digits[1].toInt(), digits[2].toInt())
            digits.size == 1 && digits[0].length == 8 -> LocalDate.of(digits[0].take(4).toInt(), digits[0].substring(4, 6).toInt(), digits[0].takeLast(2).toInt())
            else -> null
        }
    }.getOrNull()
}

fun formatBirthday(date: LocalDate?): String = date?.let { "${it.year}년 ${it.monthValue}월 ${it.dayOfMonth}일" }.orEmpty()

/** 이름·관계·생일 입력 상태 (W3-2 새 가족 추가, W9 가족 추가, W9-2 가족 고치기 공통) */
class FamilyFormState(initial: FamilyMember?) {
    var name by mutableStateOf(initial?.name.orEmpty())
    var relation by mutableStateOf(initial?.relation)
    var birthdayText by mutableStateOf(formatBirthday(initial?.birthday))
    val birthday: LocalDate? get() = parseBirthday(birthdayText)
    val valid: Boolean get() = name.isNotBlank() && relation != null
}

@Composable
fun rememberFamilyFormState(initial: FamilyMember? = null): FamilyFormState = remember(initial?.id) { FamilyFormState(initial) }

/**
 * 이름(Text Field, 필수) · 관계(Tab Chip 하나: 아들·딸·엄마·아빠·할머니·할아버지·그 밖에) · 생일(직접 입력, 비워도 됨)
 * 아래 계산한 나이 한 줄. 날짜 고르기가 없으니 생일 칸의 달력 아이콘은 뺀다(한나 주석).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FamilyForm(
    state: FamilyFormState,
    today: LocalDate,
    ageHint: String,
    modifier: Modifier = Modifier,
) {
    val focus = LocalFocusManager.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CarDiaryTextField(label = "이름", value = state.name, onValueChange = { if (it.length <= 10) state.name = it }, placeholder = "예: 하린")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CarDiaryText("관계", style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap), verticalArrangement = Arrangement.spacedBy(CarDiaryDimens.ChipGap)) {
                Relation.entries.forEach { r ->
                    // 고르기는 키보드가 필요 없으니 입력 칸 포커스를 풀어 키보드를 닫는다
                    CarDiaryTabChip(r.label, selected = state.relation == r, onClick = { state.relation = r; focus.clearFocus() })
                }
            }
        }
        CarDiaryTextField(
            label = "생일",
            value = state.birthdayText,
            onValueChange = { state.birthdayText = it },
            placeholder = "예: 2021년 8월 12일",
            keyboardType = KeyboardType.Text,
        )
        val age = state.birthday?.let { java.time.Period.between(it, today).years }
        SubText(if (age != null) "지금 ${age}살이에요. $ageHint" else "생일은 비워도 저장돼요. $ageHint")
    }
}
