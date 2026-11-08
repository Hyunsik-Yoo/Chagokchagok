package com.hyunsikyoo.pleosapp.feature.diary

import com.hyunsikyoo.pleosapp.data.tts.NarrationState
import com.hyunsikyoo.pleosapp.domain.DiaryFormat
import com.hyunsikyoo.pleosapp.domain.model.Diary
import com.hyunsikyoo.pleosapp.domain.model.FluentEmoji
import com.hyunsikyoo.pleosapp.domain.model.HighlightType
import com.hyunsikyoo.pleosapp.domain.model.Mileage
import com.hyunsikyoo.pleosapp.domain.model.PieceKind
import com.hyunsikyoo.pleosapp.domain.model.Sticker
import java.time.Duration

data class RoutePointUi(val place: String, val time: String, val tag: String)

/** 명장면 한 줄 / 기록 팝업 한 줄 */
data class RecordUi(
    val time: String,
    val title: String,
    val desc: String?,
    val emoji: FluentEmoji,
    val isHighlight: Boolean,
)

/** 일기 화면(와이어프레임 C) 상태. 값이 없는 칸은 null → 화면에서 숨긴다 */
data class DiaryUiState(
    val date: java.time.LocalDate,
    // 헤더
    val hasPrev: Boolean,
    val hasNext: Boolean,
    // 요약 줄 (2~5번)
    val dateLabel: String,
    val weatherLabel: String?,
    val temperatureLabel: String?,
    val from: RoutePointUi,
    val to: RoutePointUi?,
    val distanceLabel: String,
    val durationLabel: String,
    val passengerNames: String?,
    val passengerCount: Int,
    // 일기 종이 (6~10번)
    val title: String,
    val sentences: List<String>,
    val stickers: List<Sticker>,
    val narration: NarrationState,
    val ttsAvailable: Boolean,
    // 오른쪽 (11~13번)
    val highlights: List<RecordUi>,
    val records: List<RecordUi>,
    val recordSummary: String,
    val pieces: List<String>,
    val pieceMoreCount: Int,
    val allPieces: List<String>,
    val mileageLabel: String,
    val mileageProgress: Float,
    val mileageCaption: String,
) {
    /** 기록이 3개 이하면 전체보기 숨김 */
    val showAllRecords: Boolean get() = records.size > 3
}

/** 화면에 보이는 조각 수(+N 칩 포함 최대 4칸) */
private const val PIECE_SLOTS = 4

fun Diary.toUiState(
    mileage: Mileage,
    narration: NarrationState,
    ttsAvailable: Boolean,
    hasPrev: Boolean,
    hasNext: Boolean,
): DiaryUiState {
    val first = trips.first()
    val last = trips.last()
    val distanceKm = trips.sumOf { it.distanceKm }
    val duration = trips.fold(Duration.ZERO) { acc, t -> acc + Duration.between(t.startAt, t.endAt) }
    val passengers = trips.flatMap { it.passengers }.distinctBy { it.id }
    val highlightKeys = highlights.map { it.type to it.at }.toSet()

    fun recordUi(h: com.hyunsikyoo.pleosapp.domain.model.Highlight) = RecordUi(
        time = DiaryFormat.time(h.at),
        title = h.title,
        desc = h.desc,
        emoji = h.type.emoji,
        isHighlight = (h.type to h.at) in highlightKeys,
    )

    // 조각: 동네 이야기 맨 앞, 소나 컨디션 맨 끝 1개. 넘치면 +N
    val sortedPieces = pieces.sortedWith(
        compareBy({ it.kind == PieceKind.CarCondition }, { it.kind != PieceKind.Neighborhood }, { it.rank }),
    )
    val carCondition = sortedPieces.lastOrNull { it.kind == PieceKind.CarCondition }
    val others = sortedPieces.filter { it.kind != PieceKind.CarCondition }
    val shownPieces = if (sortedPieces.size <= PIECE_SLOTS) {
        sortedPieces
    } else {
        val room = PIECE_SLOTS - 1 // 마지막 칸은 +N
        others.take(if (carCondition != null) room - 1 else room) + listOfNotNull(carCondition)
    }

    return DiaryUiState(
        date = date,
        hasPrev = hasPrev,
        hasNext = hasNext,
        dateLabel = DiaryFormat.date(date),
        weatherLabel = weather?.label,
        temperatureLabel = weather?.temperatureC?.let { "${it}°C" },
        from = RoutePointUi(first.startPlace.name, DiaryFormat.time(first.startAt), "출발"),
        to = last.endPlace?.let { RoutePointUi(it.name, DiaryFormat.time(highlights.lastOrNull { h -> h.type == HighlightType.Arrival }?.at ?: last.endAt), "도착") },
        distanceLabel = DiaryFormat.distance(distanceKm),
        durationLabel = DiaryFormat.duration(duration),
        passengerNames = passengers.takeIf { it.isNotEmpty() }?.let { DiaryFormat.names(it.map { p -> p.name }) },
        passengerCount = passengers.size,
        title = title,
        sentences = sentences,
        stickers = stickers,
        narration = narration,
        ttsAvailable = ttsAvailable,
        highlights = highlights.sortedBy { it.at }.map(::recordUi),
        // 명장면 전체보기(W-D2): 시간 정보가 있는 명장면 후보만 (출발·비 그침 같은 메모는 뺀다)
        records = records.filter { it.type != HighlightType.Note }.sortedBy { it.at }.map(::recordUi),
        recordSummary = buildString {
            append("${date.monthValue}월 ${date.dayOfMonth}일 · ${first.startPlace.name}")
            last.endPlace?.let { append(" → ${it.name}") }
            append(" · ${records.size}개")
        },
        pieces = shownPieces.map { it.label },
        pieceMoreCount = sortedPieces.size - shownPieces.size,
        allPieces = sortedPieces.map { it.label },
        mileageLabel = DiaryFormat.distance(mileage.totalKm),
        mileageProgress = DiaryFormat.mileageProgress(mileage),
        mileageCaption = DiaryFormat.mileageCaption(mileage),
    )
}
