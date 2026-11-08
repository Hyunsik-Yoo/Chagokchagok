package com.hyunsikyoo.pleosapp.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/*
 * 일기 데이터 구조 — 팀 시안 "일기 화면 개발 핸드오프 · 데이터 구조(안)".
 * 저장은 차량 로컬 DB(Room) 예정. 지금은 시연 데이터로 채운다.
 */

/** 장소. [isHome]은 설정(W9)에 등록한 집 */
data class Place(
    val name: String,
    val isHome: Boolean = false,
)

/** 트립 하나 (시동·기어 P 해제 ~ 트립 종료) */
data class Trip(
    val id: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val startPlace: Place,
    val endPlace: Place?,
    val distanceKm: Double,
    val passengers: List<FamilyMember>,
    /** 이어 달리기로 합친 트립 */
    val mergedFrom: List<Long> = emptyList(),
)

/** 관계 (새 가족 추가의 Tab Chip). 친구는 "그 밖에" */
enum class Relation(val label: String, val isChild: Boolean = false) {
    Son("아들", true),
    Daughter("딸", true),
    Mom("엄마"),
    Dad("아빠"),
    Grandma("할머니"),
    Grandpa("할아버지"),
    Other("그 밖에"),
}

/** 가족(또는 함께 탄 친구). 나이는 생일로 계산해 일기 문장에 쓴다. 생일은 비워도 된다 */
data class FamilyMember(
    val id: Long,
    val name: String,
    val relation: Relation = Relation.Other,
    val birthday: LocalDate? = null,
) {
    val isChild: Boolean get() = relation.isChild

    /** 만 나이 */
    fun ageOn(date: LocalDate): Int? = birthday?.let { java.time.Period.between(it, date).years }
}

/** 자리 4칸 (차 배치 그대로) */
enum class Seat(val label: String) {
    Driver("운전석"),
    Passenger("조수석"),
    RearLeft("뒷자리 왼쪽"),
    RearRight("뒷자리 오른쪽"),
}

/**
 * 명장면 종류 — 핸드오프 "명장면 종류" 표.
 * [rank]: 기록 선정 우선순위(1 필수 → 6 소나 컨디션). 낮을수록 먼저.
 */
enum class HighlightType(val emoji: FluentEmoji, val rank: Int) {
    Arrival(FluentEmoji.HouseWithGarden, 1),
    FirstVisit(FluentEmoji.Sparkles, 1),
    Milestone(FluentEmoji.ChequeredFlag, 1),
    Nap(FluentEmoji.SleepingFace, 2),
    Rain(FluentEmoji.Droplet, 3),
    FirstSnow(FluentEmoji.Snowflake, 3),
    Fog(FluentEmoji.Fog, 3),
    FullMoon(FluentEmoji.FullMoon, 3),
    StraightRoad(FluentEmoji.Motorway, 4),
    Rest(FluentEmoji.HotBeverage, 4),
    MountainRoad(FluentEmoji.Mountain, 4),
    Window(FluentEmoji.LeafFlutteringInWind, 5),
    /** 시간 정보가 있지만 명장면 후보가 아닌 기록(출발, 비 그침 등) */
    Note(FluentEmoji.Sun, 6),
}

/** 시간 정보가 있는 기록. 명장면 후보 */
data class Highlight(
    val tripId: Long,
    val type: HighlightType,
    val at: LocalDateTime,
    /** 12자 이내 */
    val title: String,
    /** 16자 이내 */
    val desc: String? = null,
    val score: Int = 0,
    /** 긍정적인 기록만 스티커가 된다 */
    val positive: Boolean = true,
)

/** 조각 분류. 동네 이야기는 맨 앞, 소나 컨디션은 맨 끝 */
enum class PieceKind { Neighborhood, Weather, Habit, CarCondition }

/** 시간 정보가 없는 상태 기록 (벚꽃 사흘째, 열선 켜 둠, 와셔액 부족) */
data class Piece(
    val tripId: Long,
    val kind: PieceKind,
    /** 8자 이내 */
    val label: String,
    val rank: Int,
)

/** 일기 종이 위 스티커 하나. [slot]은 고정 빈자리 번호 */
data class Sticker(
    val emoji: FluentEmoji,
    val slot: Int,
)

/** 날씨 요약. 와이퍼(비) + 외부 날씨 API + HVAC 외기 온도 */
data class WeatherSummary(
    val label: String,
    val temperatureC: Int?,
)

/** 하루 1편 일기. 하루 기준은 출발 날짜 */
data class Diary(
    val date: LocalDate,
    val trips: List<Trip>,
    val weather: WeatherSummary?,
    val title: String,
    /** 문장 단위. 들려주기 형광펜이 문장 단위로 움직인다 */
    val sentences: List<String>,
    /** 그날 기록 전체(시간순, 최대 30) */
    val records: List<Highlight>,
    /** 화면에 나오는 명장면 3개(도착 1 + 점수 상위 2) */
    val highlights: List<Highlight>,
    val pieces: List<Piece>,
    val stickers: List<Sticker>,
    val hidden: Boolean = false,
    /** 폰에서 덧붙인 한마디. 일기 글은 바꾸지 않고 들려주기 끝에 이어 읽는다 */
    val phoneNote: String? = null,
)

/** 아이별 누적 거리 */
data class Mileage(
    val childId: Long,
    val totalKm: Int,
    val nextMilestoneKm: Int,
)

/** 마일스톤 단계 (km) */
val MilestoneSteps = listOf(100, 500, 1_000, 3_000, 5_000, 10_000)

/**
 * Fluent 3D 이모지 (MIT, microsoft/fluentui-emoji) 120px PNG.
 * 핸드오프 "확인 필요: Fluent 이모지 PNG ↔ 명장면 종류 매핑 표 확정" 전까지 쓰는 1차 매핑.
 */
enum class FluentEmoji(val char: String) {
    Droplet("💧"),
    Motorway("🛣️"),
    HouseWithGarden("🏡"),
    Sparkles("✨"),
    SunBehindCloud("⛅"),
    Balloon("🎈"),
    HotBeverage("☕"),
    Mountain("⛰️"),
    LeafFlutteringInWind("🍃"),
    ChequeredFlag("🏁"),
    Snowflake("❄️"),
    Star("⭐"),
    CherryBlossom("🌸"),
    Fog("🌫️"),
    FullMoon("🌕"),
    Sun("☀️"),
    SleepingFace("😴"),
}
