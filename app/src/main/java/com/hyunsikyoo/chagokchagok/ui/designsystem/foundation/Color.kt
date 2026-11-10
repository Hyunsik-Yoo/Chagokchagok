package com.hyunsikyoo.chagokchagok.ui.designsystem.foundation

import androidx.compose.ui.graphics.Color

/**
 * 색 토큰 — 팀 디자인 시스템 "2. 색 토큰 (앱에서 쓰는 것)" + "6. CSS 변수".
 * 회색은 따로 만들지 않고 검정에 불투명도를 준다(11/8: Surface·Line·Disabled 전부 #000 n%).
 * 새 색이 필요하면 [Palette] 안에서만 고른다.
 */
object CarDiaryColors {
    // 면
    /** 앱 창 바탕 (application 프레임 배경). 일기·주행 중·홈 공통 */
    val AppBackground = Color(0xFF99D6FF)
    val Card = Color(0xFFFFFFFF)
    /** 자리 표시 바탕, 아이콘 원 바탕 */
    val Surface = Color(0x0D000000)
    /** Locked 카드 바탕 */
    val Surface2 = Color(0x0D000000)

    // 선
    /** 기본 테두리 2px, 하드 그림자 */
    val Line = Color(0xFF000000)
    /** 연한 테두리 (List Row Default, 점선 칸) */
    val LineSoft = Color(0x33000000)
    /** Calendar Day 빈칸 테두리, Locked 배지 */
    val LineLight = Color(0x0D000000)

    // 글자 — 검정 100%·50%, Main 파랑만 쓴다
    val Text = Color(0xFF000000)
    val TextSub = Color(0x80000000)
    /** Pleos 기본 글자색 (Route Point 시각, Tag 글자) */
    val TextPleos = Color(0xFF000000)
    /** Milestone 캡션 */
    val TextCaption = Color(0xB3000000)
    val TextDisabled = Color(0x59000000)

    // 강조
    /** Base/Main — 개수 숫자, Secondary 버튼 몸통 */
    val Main = Color(0xFF5294FF)
    /** Palette/Orange/main — Primary 버튼 몸통, Progress Bar 채움 */
    val Primary = Color(0xFFFF7A05)
    /** Milestone ✓ 달성, 핸드오프 번호 배지 */
    val AccentLink = Color(0xFF1F6FCC)
    /** Toggle On 바탕 */
    val Green = Color(0xFF05E17A)
    /** Status Pill Accent 바탕 (기록 중) */
    val GreenBackground = Color(0xFFDEFCE9)
    /** Status Pill Accent 점 */
    val Emerald = Color(0xFF00D696)
    /** Banner 바탕, 들려주기 형광펜, Milestone Done 배지 */
    val YellowBackground = Color(0xFFFDF7C4)
    /** 선택 상태 (List Row Selected, Seat Card Filled, Calendar Selected) */
    val SkyBackground = Color(0xFFDDEFFD)
    /** Piece Tag(조각 칩) 바탕 */
    val PieceBackground = Color(0xFFFFEC99)
    /** Avatar 자리 표시 바탕 (Figma 컴포넌트 그림 기준, Palette/Amber/main) */
    val AvatarBackground = Color(0xFFFFBF00)

    // 비활성
    val DisabledFill = Color(0x1A000000)
    val DisabledStroke = Color(0x40000000)

    /** Tag 바탕 (#000 10%) */
    val TagBackground = Color(0x1A000000)
    /** Toast 바탕 (#000 85%) */
    val Toast = Color(0xD9000000)
    /** 팝업 뒤 덮개 (검정 45%) */
    val Scrim = Color(0x73000000)

    /** Neobrutalism 팔레트 전체 (main / background). 위에 없는 색은 아직 안 쓴 것 */
    object Palette {
        val RedMain = Color(0xFFFF6164); val RedBackground = Color(0xFFFFE0E0)
        val OrangeMain = Color(0xFFFF7A05); val OrangeBackground = Color(0xFFFFEDD6)
        val AmberMain = Color(0xFFFFBF00); val AmberBackground = Color(0xFFFEF3C8)
        val YellowMain = Color(0xFFFACC00); val YellowBackground = Color(0xFFFDF7C4)
        val LimeMain = Color(0xFF8AE500); val LimeBackground = Color(0xFFE7F7CF)
        val GreenMain = Color(0xFF05E17A); val GreenBackground = Color(0xFFDEFCE9)
        val EmeraldMain = Color(0xFF00D696); val EmeraldBackground = Color(0xFFD0FBEA)
        val TealMain = Color(0xFF00D6BD); val TealBackground = Color(0xFFCCFAF2)
        val CyanMain = Color(0xFF00C8F0); val CyanBackground = Color(0xFFCDF6FE)
        val SkyMain = Color(0xFF0099FF); val SkyBackground = Color(0xFFDDEFFD)
        val BlueMain = Color(0xFF5294FF); val BlueBackground = Color(0xFFDCEBFE)
        val IndigoMain = Color(0xFF858DFF); val IndigoBackground = Color(0xFFE0E2FF)
        val VioletMain = Color(0xFFA985FF); val VioletBackground = Color(0xFFEEE6FE)
        val PurpleMain = Color(0xFFCA7AFF); val PurpleBackground = Color(0xFFF2E5FF)
        val FuchsiaMain = Color(0xFFE96BFF); val FuchsiaBackground = Color(0xFFFBE5FF)
        val PinkMain = Color(0xFFFC64AB); val PinkBackground = Color(0xFFFCE9F3)
        val RoseMain = Color(0xFFFF6678); val RoseBackground = Color(0xFFFFE0E3)

        /** 카탈로그 표시용: 이름, main, background */
        val all = listOf(
            Triple("Red", RedMain, RedBackground),
            Triple("Orange", OrangeMain, OrangeBackground),
            Triple("Amber", AmberMain, AmberBackground),
            Triple("Yellow", YellowMain, YellowBackground),
            Triple("Lime", LimeMain, LimeBackground),
            Triple("Green", GreenMain, GreenBackground),
            Triple("Emerald", EmeraldMain, EmeraldBackground),
            Triple("Teal", TealMain, TealBackground),
            Triple("Cyan", CyanMain, CyanBackground),
            Triple("Sky", SkyMain, SkyBackground),
            Triple("Blue", BlueMain, BlueBackground),
            Triple("Indigo", IndigoMain, IndigoBackground),
            Triple("Violet", VioletMain, VioletBackground),
            Triple("Purple", PurpleMain, PurpleBackground),
            Triple("Fuchsia", FuchsiaMain, FuchsiaBackground),
            Triple("Pink", PinkMain, PinkBackground),
            Triple("Rose", RoseMain, RoseBackground),
        )
    }
}
