package com.hyunsikyoo.chagokchagok.ui.designsystem.foundation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * 간격·크기 토큰 — 팀 디자인 시스템 "1. 화면 틀", "5. 선·모서리·그림자·간격".
 * 값은 2560×1440 화면 px. Pleos 에뮬레이터는 160dpi라 1dp = 1px.
 */
object CarDiaryDimens {
    // 화면 틀
    /** 앱 창 크기 (위치 x840 y96) */
    val AppWidth = 1700.dp
    val AppHeight = 1184.dp
    /** 앱 창 안쪽 여백 */
    val PagePadding = 40.dp
    /** 헤더 높이. 내용은 y150부터 */
    val HeaderHeight = 110.dp
    /** 칸 사이 */
    val ColumnGap = 24.dp
    /** 왼쪽 큰 칸(또는 일기 종이 959) */
    val ColumnLarge = 1060.dp
    val ColumnDiaryPaper = 959.dp
    /** 오른쪽 칸(또는 629) */
    val ColumnSmall = 536.dp
    val ColumnSmallWide = 629.dp

    // 선
    val Stroke = 2.dp

    // 카드
    val CardPadding = 34.dp
    val CardGap = 24.dp
    val CardSmallPadding = 22.dp
    val CardSmallGap = 10.dp

    // 버튼·칩
    /** 버튼 겉 높이 (Body 82 + 그림자 자리 4) */
    val ButtonHeight = 86.dp
    val ButtonBodyHeight = 82.dp
    val ButtonGap = 16.dp
    val ChipGap = 12.dp

    // 팝업
    val PopupWidth = 1200.dp
    val PopupHeight = 776.dp
}

/** 모서리 토큰 */
object CarDiaryShapes {
    /** 버튼·칩·태그·토글·진행 막대 (100) */
    val Pill = RoundedCornerShape(percent = 50)
    /** 카드·아이콘 버튼 */
    val Card = RoundedCornerShape(24.dp)
    val Popup = RoundedCornerShape(48.dp)
    val AppWindow = RoundedCornerShape(40.dp)
    val ListRow = RoundedCornerShape(20.dp)
    val CalendarDay = RoundedCornerShape(18.dp)
    /** 자리 칸 (Seat Card) */
    val Seat = RoundedCornerShape(28.dp)
}

/** 하드 섀도(흐림 0) 오프셋. 화면당 Hard 카드 1장만 */
object CarDiaryShadows {
    val Card = 6.dp
    /** 버튼·알약·배너 */
    val Soft = 4.dp
    val Popup = 8.dp
}
