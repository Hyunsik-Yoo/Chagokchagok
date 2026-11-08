package com.hyunsikyoo.pleosapp.domain

import com.hyunsikyoo.pleosapp.domain.model.Mileage
import com.hyunsikyoo.pleosapp.domain.model.MilestoneSteps
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** 화면 문구 만들기 — 핸드오프 표기 규칙 */
object DiaryFormat {
    private val korean = Locale.KOREAN
    private val time24 = DateTimeFormatter.ofPattern("HH:mm")
    private val number = NumberFormat.getIntegerInstance(korean)

    /** 서울↔부산 왕복 환산 기준 (고정) */
    private const val SEOUL_BUSAN_ROUND_TRIP_KM = 800

    /** "11월 1일 일요일" */
    fun date(date: LocalDate): String =
        "${date.monthValue}월 ${date.dayOfMonth}일 ${date.dayOfWeek.getDisplayName(TextStyle.FULL, korean)}"

    /** "10:40" (24시간제) */
    fun time(at: LocalDateTime): String = at.format(time24)

    /** "87km" */
    fun distance(km: Double): String = "${number.format(Math.round(km))}km"

    /** "3,214km" */
    fun distance(km: Int): String = "${number.format(km)}km"

    /** "1시간 48분" / "35분" */
    fun duration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        return when {
            hours == 0L -> "${minutes}분"
            minutes == 0 -> "${hours}시간"
            else -> "${hours}시간 ${minutes}분"
        }
    }

    /** 이름 목록 "아빠, 엄마, 준" */
    fun names(names: List<String>): String = names.joinToString(", ")

    /** 다음 마일스톤 대비 비율 (3,214/5,000 = 0.64) */
    fun mileageProgress(mileage: Mileage): Float =
        (mileage.totalKm.toFloat() / mileage.nextMilestoneKm).coerceIn(0f, 1f)

    /** "서울↔부산 4번 왕복 · 5,000km까지 1,786km" */
    fun mileageCaption(mileage: Mileage): String {
        val roundTrips = mileage.totalKm / SEOUL_BUSAN_ROUND_TRIP_KM
        val remain = (mileage.nextMilestoneKm - mileage.totalKm).coerceAtLeast(0)
        val head = if (roundTrips > 0) "서울↔부산 ${roundTrips}번 왕복" else "첫 마일스톤까지 달리는 중"
        return "$head · ${distance(mileage.nextMilestoneKm)}까지 ${distance(remain)}"
    }

    /** 누적 거리로 다음 마일스톤 찾기 */
    fun nextMilestone(totalKm: Int): Int = MilestoneSteps.firstOrNull { it > totalKm } ?: MilestoneSteps.last()
}
