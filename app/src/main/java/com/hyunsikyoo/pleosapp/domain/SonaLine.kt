package com.hyunsikyoo.pleosapp.domain

import com.hyunsikyoo.pleosapp.data.demo.DiarySummary
import java.time.LocalDate
import java.time.MonthDay
import java.time.temporal.ChronoUnit

/**
 * 홈 "소나의 한마디" (W2 ⑥). 말하는 이는 소나. 앱을 열 때 처음 맞는 하나만, 같은 날엔 같은 문장, 최대 3줄.
 * 순서: ① 비나 눈 예보 → ② 날짜 표 기간 또는 첫눈 → ③ 아이가 5일 넘게 안 탐 → ④ 2~4주 전 일기 추억.
 */
object SonaLine {

    /** 앱 안 고정 날짜 표 */
    private val seasons = listOf(
        Triple(MonthDay.of(3, 25), MonthDay.of(4, 10), "벚꽃이 피기 시작했대. 꽃잎 날리는 길로 같이 달려 볼까?"),
        Triple(MonthDay.of(7, 20), MonthDay.of(8, 20), "여름 방학이다! 이번엔 바다 보러 갈까?"),
        Triple(MonthDay.of(10, 20), MonthDay.of(11, 10), "단풍이 한창이래. 빨간 나무 사이로 달려 보고 싶어."),
    )

    fun pick(
        today: LocalDate,
        carName: String,
        childName: String,
        summaries: List<DiarySummary>,
        /** 외부 날씨 API 예보(호출 1개 추가 예정). 지금은 연결 전이라 null */
        forecast: String? = null,
    ): String {
        // ① 비나 눈 예보
        forecast?.let { return it }
        // ② 날짜 표 기간
        val md = MonthDay.from(today)
        seasons.firstOrNull { (from, to, _) -> !md.isBefore(from) && !md.isAfter(to) }?.let { return it.third }
        // ③ 아이가 5일 넘게 안 탐
        val lastRide = summaries.filter { !it.hidden && !it.date.isAfter(today) }.maxOfOrNull { it.date }
        if (lastRide != null && ChronoUnit.DAYS.between(lastRide, today) > 5) {
            return "${childName}이 못 본 지 ${ChronoUnit.DAYS.between(lastRide, today)}일째야. 보고 싶다!"
        }
        // ④ 2~4주 전 일기 추억
        summaries.filter { !it.short && ChronoUnit.DAYS.between(it.date, today) in 14..28 }.maxByOrNull { it.date }?.let {
            return "지난번 ${it.place} 가는 길 기억나? 또 같이 가자."
        }
        return "오늘은 어디로 달려 볼까? $carName 가 기다리고 있을게."
    }
}
