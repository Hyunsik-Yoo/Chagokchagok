package com.hyunsikyoo.chagokchagok.ui.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * 디자인 시스템 Icon (Figma 53:3909) 26종.
 * Feather Icons 4.29.2 (MIT, Cole Bemis) 원본 SVG를 변환해 만든 파일 — 손으로 고치지 말고 다시 생성한다.
 * 기본 32×32, 선 2.67(= 24 기준 2). 색은 Icon(tint = ...)으로 덮어쓴다.
 */
object CarDiaryIcons {
    /** arrow-left */
    val ArrowLeft: ImageVector by lazy { feather("arrow-left", "M19 12L5 12", "M12 19 L5 12 L12 5") }
    /** bell */
    val Bell: ImageVector by lazy { feather("bell", "M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9", "M13.73 21a2 2 0 0 1-3.46 0") }
    /** calendar */
    val Calendar: ImageVector by lazy { feather("calendar", "M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1 -2 2h-14a2 2 0 0 1 -2 -2v-14a2 2 0 0 1 2 -2Z", "M16 2L16 6", "M8 2L8 6", "M3 10L21 10") }
    /** camera */
    val Camera: ImageVector by lazy { feather("camera", "M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z", "M8 13a4 4 0 1 0 8 0a4 4 0 1 0 -8 0") }
    /** check */
    val Check: ImageVector by lazy { feather("check", "M20 6 L9 17 L4 12") }
    /** chevron-down */
    val ChevronDown: ImageVector by lazy { feather("chevron-down", "M6 9 L12 15 L18 9") }
    /** chevron-left */
    val ChevronLeft: ImageVector by lazy { feather("chevron-left", "M15 18 L9 12 L15 6") }
    /** chevron-right */
    val ChevronRight: ImageVector by lazy { feather("chevron-right", "M9 18 L15 12 L9 6") }
    /** clock */
    val Clock: ImageVector by lazy { feather("clock", "M2 12a10 10 0 1 0 20 0a10 10 0 1 0 -20 0", "M12 6 L12 12 L16 14") }
    /** edit-2 */
    val Edit2: ImageVector by lazy { feather("edit-2", "M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z") }
    /** gift */
    val Gift: ImageVector by lazy { feather("gift", "M20 12 L20 22 L4 22 L4 12", "M2 7h20v5h-20Z", "M12 22L12 7", "M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z", "M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z") }
    /** home */
    val Home: ImageVector by lazy { feather("home", "M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z", "M9 22 L9 12 L15 12 L15 22") }
    /** lock */
    val Lock: ImageVector by lazy { feather("lock", "M5 11h14a2 2 0 0 1 2 2v7a2 2 0 0 1 -2 2h-14a2 2 0 0 1 -2 -2v-7a2 2 0 0 1 2 -2Z", "M7 11V7a5 5 0 0 1 10 0v4") }
    /** map-pin */
    val MapPin: ImageVector by lazy { feather("map-pin", "M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z", "M9 10a3 3 0 1 0 6 0a3 3 0 1 0 -6 0") }
    /** mic */
    val Mic: ImageVector by lazy { feather("mic", "M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z", "M19 10v2a7 7 0 0 1-14 0v-2", "M12 19L12 23", "M8 23L16 23") }
    /** pause */
    val Pause: ImageVector by lazy { feather("pause", "M6 4h4v16h-4Z", "M14 4h4v16h-4Z") }
    /** play */
    val Play: ImageVector by lazy { feather("play", "M5 3 L19 12 L5 21 L5 3Z") }
    /** plus */
    val Plus: ImageVector by lazy { feather("plus", "M12 5L12 19", "M5 12L19 12") }
    /** send */
    val Send: ImageVector by lazy { feather("send", "M22 2L11 13", "M22 2 L15 22 L11 13 L2 9 L22 2Z") }
    /** settings */
    val Settings: ImageVector by lazy { feather("settings", "M9 12a3 3 0 1 0 6 0a3 3 0 1 0 -6 0", "M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z") }
    /** smartphone */
    val Smartphone: ImageVector by lazy { feather("smartphone", "M7 2h10a2 2 0 0 1 2 2v16a2 2 0 0 1 -2 2h-10a2 2 0 0 1 -2 -2v-16a2 2 0 0 1 2 -2Z", "M12 18L12.01 18") }
    /** star */
    val Star: ImageVector by lazy { feather("star", "M12 2 L15.09 8.26 L22 9.27 L17 14.14 L18.18 21.02 L12 17.77 L5.82 21.02 L7 14.14 L2 9.27 L8.91 8.26 L12 2Z") }
    /** trash-2 */
    val Trash2: ImageVector by lazy { feather("trash-2", "M3 6 L5 6 L21 6", "M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2", "M10 11L10 17", "M14 11L14 17") }
    /** user */
    val User: ImageVector by lazy { feather("user", "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2", "M8 7a4 4 0 1 0 8 0a4 4 0 1 0 -8 0") }
    /** volume-2 */
    val Volume2: ImageVector by lazy { feather("volume-2", "M11 5 L6 9 L2 9 L2 15 L6 15 L11 19 L11 5Z", "M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07") }
    /** x */
    val X: ImageVector by lazy { feather("x", "M18 6L6 18", "M6 6L18 18") }

    /** 이름(Figma 변형 Name)으로 찾기 */
    val all: Map<String, ImageVector> by lazy {
        mapOf(
            "arrow-left" to ArrowLeft,
            "bell" to Bell,
            "calendar" to Calendar,
            "camera" to Camera,
            "check" to Check,
            "chevron-down" to ChevronDown,
            "chevron-left" to ChevronLeft,
            "chevron-right" to ChevronRight,
            "clock" to Clock,
            "edit-2" to Edit2,
            "gift" to Gift,
            "home" to Home,
            "lock" to Lock,
            "map-pin" to MapPin,
            "mic" to Mic,
            "pause" to Pause,
            "play" to Play,
            "plus" to Plus,
            "send" to Send,
            "settings" to Settings,
            "smartphone" to Smartphone,
            "star" to Star,
            "trash-2" to Trash2,
            "user" to User,
            "volume-2" to Volume2,
            "x" to X,
        )
    }

    private fun feather(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(
            name = "feather-$name",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            paths.forEach { d ->
                addPath(
                    pathData = PathParser().parsePathString(d).toNodes(),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
}
