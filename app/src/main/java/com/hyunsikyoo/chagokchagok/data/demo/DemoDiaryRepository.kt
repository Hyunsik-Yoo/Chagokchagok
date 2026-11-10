package com.hyunsikyoo.chagokchagok.data.demo

import com.hyunsikyoo.chagokchagok.domain.model.Diary
import com.hyunsikyoo.chagokchagok.domain.model.FamilyMember
import com.hyunsikyoo.chagokchagok.domain.model.FluentEmoji
import com.hyunsikyoo.chagokchagok.domain.model.Highlight
import com.hyunsikyoo.chagokchagok.domain.model.HighlightType
import com.hyunsikyoo.chagokchagok.domain.model.Mileage
import com.hyunsikyoo.chagokchagok.domain.model.Piece
import com.hyunsikyoo.chagokchagok.domain.model.PieceKind
import com.hyunsikyoo.chagokchagok.domain.model.Relation
import com.hyunsikyoo.chagokchagok.domain.model.Place
import com.hyunsikyoo.chagokchagok.domain.model.Seat
import com.hyunsikyoo.chagokchagok.domain.model.Sticker
import com.hyunsikyoo.chagokchagok.domain.model.Trip
import com.hyunsikyoo.chagokchagok.domain.model.WeatherSummary
import java.time.LocalDate
import java.time.LocalDateTime

/** 홈 "지난 일기"·달력에 쓰는 일기 한 줄 요약 */
data class DiarySummary(
    val date: LocalDate,
    val place: String,
    val weather: String,
    val distanceKm: Int,
    val highlightCount: Int,
    val firstEmoji: FluentEmoji,
    /** 반복 길(등원길)의 짧은 일기 */
    val short: Boolean = false,
    val hidden: Boolean = false,
)

/**
 * 시연 데이터 — 팀 시안의 "11월 1일 할머니 댁" 일기와 와이어프레임 예시 숫자 그대로.
 * 실제 기록·일기 생성(Room + Pleos SDK)이 붙기 전까지 화면을 채운다.
 */
object DemoDiaryRepository {

    val dad = FamilyMember(1, "아빠", Relation.Dad, LocalDate.of(1991, 2, 14))
    val mom = FamilyMember(2, "엄마", Relation.Mom, LocalDate.of(1992, 9, 1))
    val jun = FamilyMember(3, "준", Relation.Son, LocalDate.of(2020, 5, 3))
    val family = listOf(dad, mom, jun)

    /** 설정(W9) "가족과 자리" 기본값 */
    val defaultSeats: Map<Seat, FamilyMember?> = mapOf(
        Seat.Driver to dad,
        Seat.Passenger to mom,
        Seat.RearLeft to jun,
        Seat.RearRight to null,
    )

    /** 시연 "오늘" — 달력 시안의 오늘(11/8). 가장 최근 긴 일기는 11/1 */
    val today: LocalDate = LocalDate.of(2026, 11, 8)
    private val diaryDay: LocalDate = LocalDate.of(2026, 11, 1)
    private fun at(h: Int, m: Int) = LocalDateTime.of(2026, 11, 1, h, m)

    private val trip = Trip(
        id = 1,
        startAt = at(10, 40),
        endAt = at(12, 28),
        startPlace = Place("우리 집", isHome = true),
        endPlace = Place("할머니 댁"),
        distanceKm = 87.0,
        passengers = listOf(dad, mom, jun),
    )

    /** 그날 기록 전체 (시간순) — 와이어프레임 C 명장면 팝업 예시 */
    private val records = listOf(
        Highlight(1, HighlightType.Note, at(10, 40), "우리 집에서 출발", "바깥 12°C"),
        Highlight(1, HighlightType.Rain, at(10, 42), "비가 오기 시작했어", "와이퍼 1시간 8분", score = 80),
        Highlight(1, HighlightType.StraightRoad, at(11, 5), "쭉 뻗은 길 40분", "고속도로", score = 60),
        Highlight(1, HighlightType.Rest, at(11, 30), "휴게소에서 쉬었어", "15분 정차", score = 50),
        Highlight(1, HighlightType.Note, at(11, 50), "비가 그쳤어", "바깥 14°C", score = 40),
        Highlight(1, HighlightType.MountainRoad, at(12, 5), "구불구불 산길", "핸들 많이 돌림", score = 45),
        Highlight(1, HighlightType.Window, at(12, 15), "창문 열고 바람", "뒷창문 5분", score = 40),
        Highlight(1, HighlightType.Arrival, at(12, 20), "할머니 댁 도착", "처음 가 본 길", score = 100),
    )

    /** 주행 중 화면 시연: 이 순서로 명장면이 하나씩 쌓인다 */
    val drivingScript: List<Highlight> = listOf(records[1], records[2], records[3], records[5])

    private val diary = Diary(
        date = diaryDay,
        trips = listOf(trip),
        weather = WeatherSummary("비온 뒤 갬", 14),
        title = "할머니 댁에 다녀온 날",
        sentences = listOf(
            "오늘은 할머니 댁에 다녀왔어!",
            "가는 길에 비가 와서 와이퍼가 바빴는데,",
            "조금 지나니 해가 나왔어.",
            "쭉 뻗은 고속도로를 40분이나 달렸지.",
            "휴게소에서 15분 쉬고 나니 바깥은 14°C로 포근해졌어.",
            "구불구불 산길을 지나 준이가 창문을 열어 바람을 맞았지.",
            "처음 가 본 길인데 준이는 끝까지 벨트를 잘 맸어.",
            "대견해!",
        ),
        records = records,
        highlights = listOf(records[1], records[2], records[7]),
        pieces = listOf(
            Piece(1, PieceKind.Neighborhood, "벚꽃 흩날리며", 1),
            Piece(1, PieceKind.Weather, "포근했던 자리", 2),
            Piece(1, PieceKind.Habit, "준이 자리 열선", 3),
            Piece(1, PieceKind.Weather, "안개 조금", 4),
            Piece(1, PieceKind.Habit, "에코 모드", 5),
            Piece(1, PieceKind.CarCondition, "타이어 바람이 부족해", 6),
        ),
        stickers = listOf(
            Sticker(FluentEmoji.Sparkles, 0),
            Sticker(FluentEmoji.SunBehindCloud, 2),
            Sticker(FluentEmoji.Balloon, 3),
        ),
        phoneNote = "준이가 휴게소에서 소떡소떡 두 개 먹음",
    )

    private fun simpleDiary(
        date: LocalDate,
        place: String,
        weather: String,
        km: Double,
        minutes: Long,
        title: String,
        sentences: List<String>,
        highlight: Highlight,
        stickers: List<Sticker>,
    ): Diary {
        val start = date.atTime(10, 0)
        return diary.copy(
            date = date,
            trips = listOf(trip.copy(id = date.toEpochDay(), startAt = start, endAt = start.plusMinutes(minutes), endPlace = Place(place), distanceKm = km)),
            weather = WeatherSummary(weather, 15),
            title = title,
            sentences = sentences,
            records = listOf(highlight),
            highlights = listOf(highlight),
            pieces = emptyList(),
            stickers = stickers,
            phoneNote = null,
        )
    }

    private val olderDiaries = listOf(
        simpleDiary(
            LocalDate.of(2026, 10, 19), "을왕리 바다", "흐림", 64.0, 70, "바다에 다녀온 날",
            listOf("오늘은 을왕리 바다에 다녀왔어.", "창문 열고 바다 냄새를 맡았지.", "준이랑 3,000km를 넘은 날이야!"),
            Highlight(19, HighlightType.Milestone, LocalDateTime.of(2026, 10, 19, 11, 10), "3,000km 넘었어", score = 100),
            listOf(Sticker(FluentEmoji.ChequeredFlag, 0), Sticker(FluentEmoji.LeafFlutteringInWind, 3)),
        ),
        simpleDiary(
            LocalDate.of(2026, 10, 26), "키즈카페", "맑음", 23.0, 35, "키즈카페에 다녀온 날",
            listOf("오늘은 키즈카페에 다녀왔어.", "준이가 신나게 놀아서 돌아오는 길엔 쿨쿨 잤지.", "오늘도 같이 달렸어!"),
            Highlight(26, HighlightType.Arrival, LocalDateTime.of(2026, 10, 26, 10, 35), "키즈카페 도착", score = 100),
            listOf(Sticker(FluentEmoji.Sun, 1), Sticker(FluentEmoji.Star, 4)),
        ),
    )

    /** 1년 전 오늘 (W8) — 시안: 작년 같은 날 할머니 댁 */
    val yearAgo: Diary = simpleDiary(
        LocalDate.of(2025, 11, 1), "할머니 댁", "비", 87.0, 100, "할머니 댁에 다녀온 날",
        listOf("오늘은 준이랑 할머니 댁에 다녀왔어!", "가는 길에 비가 와서 와이퍼가 바빴지.", "쭉 뻗은 길을 40분이나 달렸어.", "휴게소에서 쉬고 나니 해가 나왔어."),
        Highlight(0, HighlightType.Arrival, LocalDateTime.of(2025, 11, 1, 12, 20), "할머니 댁 도착", score = 100),
        listOf(Sticker(FluentEmoji.Droplet, 0), Sticker(FluentEmoji.HouseWithGarden, 3)),
    ).copy(
        records = listOf(
            Highlight(0, HighlightType.Rain, LocalDateTime.of(2025, 11, 1, 10, 42), "비가 오기 시작했어", score = 80),
            Highlight(0, HighlightType.StraightRoad, LocalDateTime.of(2025, 11, 1, 11, 5), "쭉 뻗은 길 40분", score = 60),
            Highlight(0, HighlightType.Arrival, LocalDateTime.of(2025, 11, 1, 12, 20), "할머니 댁 도착", score = 100),
        ),
    )
    const val YEAR_AGO_MILEAGE_KM = 1_120

    /** 반복 길(등원길)의 짧은 일기 — 명장면 1개로 1~2문장 */
    private val shortDiaries = listOf(2, 3, 6, 7).map { d ->
        simpleDiary(
            LocalDate.of(2026, 11, d), "어린이집", "맑음", 4.0, 12, "어린이집 가는 길",
            listOf("오늘도 준이랑 어린이집 가는 길을 같이 달렸어.", "준이가 창밖에 별 모양 구름을 찾았대."),
            Highlight(d.toLong(), HighlightType.Arrival, LocalDateTime.of(2026, 11, d, 8, 40), "어린이집 도착", score = 100),
            listOf(Sticker(FluentEmoji.Star, 4)),
        ).copy(trips = listOf(trip.copy(id = 100L + d, startAt = LocalDateTime.of(2026, 11, d, 8, 28), endAt = LocalDateTime.of(2026, 11, d, 8, 40), endPlace = Place("어린이집"), distanceKm = 4.0)))
    }

    /** 날짜순 일기 목록 (숨긴 일기 제외). 가장 최근 "긴" 일기는 [latest] */
    fun diaries(): List<Diary> = (listOf(yearAgo) + olderDiaries + diary + shortDiaries).filterNot { it.hidden }.sortedBy { it.date }

    fun diary(date: LocalDate): Diary? = diaries().firstOrNull { it.date == date }

    /** 홈 "가장 최근 일기": 가장 최근의 긴 일기 */
    fun latest(): Diary = diary

    /** 홈·달력용 요약 (짧은 일기 포함) */
    val summaries: List<DiarySummary> = listOf(
        DiarySummary(LocalDate.of(2026, 9, 14), "할머니 댁", "맑음", 87, 3, FluentEmoji.HouseWithGarden),
        DiarySummary(LocalDate.of(2026, 9, 27), "동물원", "맑음", 41, 3, FluentEmoji.Sparkles),
        DiarySummary(LocalDate.of(2026, 10, 5), "할머니 댁", "비", 87, 2, FluentEmoji.Droplet),
        DiarySummary(LocalDate.of(2026, 10, 12), "동물원", "맑음", 41, 3, FluentEmoji.Sparkles),
        DiarySummary(LocalDate.of(2026, 10, 19), "바다(을왕리)", "흐림", 64, 3, FluentEmoji.ChequeredFlag),
        DiarySummary(LocalDate.of(2026, 10, 26), "키즈카페", "맑음", 23, 2, FluentEmoji.Sun),
        DiarySummary(LocalDate.of(2026, 11, 1), "할머니 댁", "비 온 뒤 갬", 87, 3, FluentEmoji.Droplet),
        DiarySummary(LocalDate.of(2026, 11, 2), "등원길", "맑음", 4, 1, FluentEmoji.Star, short = true),
        DiarySummary(LocalDate.of(2026, 11, 3), "등원길", "맑음", 4, 1, FluentEmoji.Star, short = true),
        DiarySummary(LocalDate.of(2026, 11, 6), "등원길", "흐림", 4, 1, FluentEmoji.Star, short = true),
        DiarySummary(LocalDate.of(2026, 11, 7), "등원길", "맑음", 4, 1, FluentEmoji.Star, short = true),
    )

    /** 사람별 누적 거리 (마일스톤 화면 사람 탭) */
    fun mileage(member: FamilyMember? = jun): Mileage {
        val total = when (member?.id) {
            jun.id -> 3_214
            dad.id -> 4_890
            mom.id -> 3_820
            else -> 5_210 // 다 같이
        }
        return Mileage(member?.id ?: 0, total, com.hyunsikyoo.chagokchagok.domain.DiaryFormat.nextMilestone(total))
    }

    /** 마일스톤 달성일 (준 기준) */
    val milestoneDates: Map<Int, LocalDate> = mapOf(
        100 to LocalDate.of(2026, 3, 9),
        500 to LocalDate.of(2026, 5, 2),
        1_000 to LocalDate.of(2026, 6, 21),
        3_000 to LocalDate.of(2026, 10, 19),
    )

    val firstDiaryDate: LocalDate = LocalDate.of(2026, 3, 2)
    const val DIARY_COUNT = 92
    const val LONG_DIARY_COUNT = 18
    const val SHORT_DIARY_COUNT = 74

    /** 자주 간 곳 */
    val frequentPlaces = listOf("할머니 댁 9번", "바다 3번", "놀이공원 2번")
}
