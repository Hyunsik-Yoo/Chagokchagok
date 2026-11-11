package com.hyunsikyoo.chagokchagok.ui.designsystem.component

import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.chagokchagok.ui.designsystem.icon.CarDiaryIcons
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import kotlin.math.cos
import kotlin.math.max

/** 지도 위 한 점 (위도·경도) */
data class RouteMapPoint(val latitude: Double, val longitude: Double)

/** 경로 위 명장면 표시 (이모지 + 읽어 줄 이름) */
data class RouteMapMarker(val point: RouteMapPoint, val icon: Painter, val label: String)

/**
 * Route Map — 그날 달린 길을 실제 지도(OpenStreetMap) 위에 그린 정지 지도. 확대·이동은 막는다.
 * 지도 위 그림은 디자인 시스템 규칙 그대로: 검정 2px 테두리, 모서리 20, 길은 검정 테두리 안에 Main 파랑.
 * - 출발: 흰 동그라미, 도착: Primary 주황 동그라미(하드 섀도), [live]면 도착 대신 "지금 여기".
 * - 명장면: 흰 동그라미 안 Fluent 이모지. 점이 2개보다 적으면 "경로를 받지 못했어요".
 * - 지도 타일은 앱 톤에 맞게 옅게(채도↓ 밝기↑). 인터넷이 없으면 타일 대신 하늘색 모눈 위에 길만 그린다.
 * - 지도 출처 "© OpenStreetMap" 표시는 지우지 않는다(라이선스 조건).
 * 움직이는 효과는 쓰지 않는다(Pleos 규칙).
 */
@Composable
fun CarDiaryRouteMap(
    points: List<RouteMapPoint>,
    modifier: Modifier = Modifier,
    markers: List<RouteMapMarker> = emptyList(),
    startLabel: String = "출발",
    endLabel: String = "도착",
    live: Boolean = false,
    emptyText: String = "경로를 받지 못했어요",
) {
    Box(
        modifier
            .clip(CarDiaryShapes.ListRow)
            .background(CarDiaryColors.SkyBackground, CarDiaryShapes.ListRow)
            .semantics { contentDescription = if (points.size >= 2) "$startLabel 에서 ${if (live) "지금 위치" else endLabel}까지 달린 길 지도" else emptyText },
    ) {
        if (points.size < 2) {
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CarDiaryIcon(CarDiaryIcons.MapPin, contentDescription = null, size = 56.dp, tint = CarDiaryColors.TextSub)
                CarDiaryText(emptyText, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
            }
            MapBorder()
            return@Box
        }
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val wPx = with(density) { maxWidth.toPx() }
            val hPx = with(density) { maxHeight.toPx() }
            val padPx = with(density) { MapPadding.toPx() }
            // 지도가 그려지기 전(첫 화면)에는 좌표만으로 맞춘 자리를 쓰고, 지도가 준비되면 지도 투영으로 바꾼다
            val fallback = remember(points, wPx, hPx) { projector(points, wPx, hPx, padPx) }
            var mapProject by remember(points) { mutableStateOf<((RouteMapPoint) -> Offset)?>(null) }
            val project = mapProject ?: fallback

            OsmBaseMap(points = points, paddingPx = padPx.toInt(), onProjectionReady = { mapProject = it })

            val screen = points.map(project)
            Canvas(Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(screen.first().x, screen.first().y)
                    screen.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(path, CarDiaryColors.Line, style = Stroke(RouteOuter.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(path, CarDiaryColors.Main, style = Stroke(RouteInner.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }

            markers.forEach { m ->
                PlacedAt(project(m.point), HighlightSize) {
                    Box(
                        Modifier
                            .size(HighlightSize)
                            .background(CarDiaryColors.Card, CircleShape)
                            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CircleShape)
                            .semantics { contentDescription = m.label },
                        contentAlignment = Alignment.Center,
                    ) { Image(m.icon, contentDescription = null, modifier = Modifier.size(HighlightSize - 16.dp)) }
                }
            }

            // 출발
            PlacedAt(screen.first(), StartSize) {
                Box(Modifier.size(StartSize).background(CarDiaryColors.Card, CircleShape).border(3.dp, CarDiaryColors.Line, CircleShape))
            }
            PinLabel(screen.first(), startLabel)
            // 도착 또는 지금 위치
            PlacedAt(screen.last(), EndSize) {
                Box(
                    Modifier
                        .size(EndSize)
                        .hardShadow(CarDiaryShadows.Soft, CircleShape)
                        .background(CarDiaryColors.Primary, CircleShape)
                        .border(3.dp, CarDiaryColors.Line, CircleShape),
                )
            }
            PinLabel(screen.last(), if (live) "지금 여기" else endLabel)

            // 지도 출처 (OpenStreetMap 라이선스)
            CarDiaryText(
                "© OpenStreetMap",
                style = CarDiaryTypography.LabelSmall,
                color = CarDiaryColors.TextSub,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .background(Color(0xCCFFFFFF), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
        MapBorder()
    }
}

/** 검정 2px 테두리 (지도 타일 위에 얹는다) */
@Composable
private fun MapBorder() {
    Box(Modifier.fillMaxSize().border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.ListRow))
}

/**
 * OpenStreetMap 바탕 지도 (osmdroid). 경로가 다 보이게 맞추고, 만지면 아무 일도 없다.
 * 지도 투영이 준비되면 [onProjectionReady]로 위경도 → 화면 좌표 함수를 넘긴다.
 */
@SuppressLint("ClickableViewAccessibility")
@Composable
private fun OsmBaseMap(points: List<RouteMapPoint>, paddingPx: Int, onProjectionReady: ((RouteMapPoint) -> Offset) -> Unit) {
    var mapRef by remember { mutableStateOf<MapView?>(null) }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().apply {
                userAgentValue = ctx.packageName // OSM 타일 정책: 앱을 알 수 있는 User-Agent
            }
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(false)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                isTilesScaledToDpi = true
                setOnTouchListener { _, _ -> true } // 정지 지도: 확대·이동 없음
                overlayManager.tilesOverlay.apply {
                    // 타일을 받기 전·인터넷이 없을 때도 하늘색 모눈으로 보이게
                    loadingBackgroundColor = CarDiaryColors.SkyBackground.toArgb()
                    loadingLineColor = Color(0x99FFFFFF).toArgb()
                    setColorFilter(SoftTiles)
                }
                mapRef = this
            }
        },
        update = { map ->
            val fit = {
                val box = BoundingBox.fromGeoPointsSafe(points.map { GeoPoint(it.latitude, it.longitude) })
                map.zoomToBoundingBox(box, false, paddingPx)
                map.post {
                    val projection = map.projection
                    onProjectionReady { p ->
                        val px = projection.toPixels(GeoPoint(p.latitude, p.longitude), null)
                        Offset(px.x.toFloat(), px.y.toFloat())
                    }
                }
            }
            // 경로가 바뀔 때만 다시 맞춘다 (투영 상태가 바뀌어 다시 그려질 때마다 맞추면 끝없이 돈다)
            if (map.tag != points) {
                map.tag = points
                if (map.width > 0 && map.height > 0) fit() else map.addOnFirstLayoutListener { _, _, _, _, _ -> fit() }
            }
        },
    )
    DisposableEffect(Unit) { onDispose { mapRef?.onDetach() } }
}

/** 지도 타일을 앱 톤에 맞게: 채도 45%, 밝게 */
private val SoftTiles = ColorMatrixColorFilter(
    ColorMatrix().apply {
        setSaturation(0.45f)
        postConcat(ColorMatrix(floatArrayOf(
            0.85f, 0f, 0f, 0f, 38f,
            0f, 0.85f, 0f, 0f, 38f,
            0f, 0f, 0.85f, 0f, 38f,
            0f, 0f, 0f, 1f, 0f,
        )))
    },
)

/** [center]를 가운데로 [size] 칸을 놓는다 */
@Composable
private fun PlacedAt(center: Offset, size: Dp, content: @Composable () -> Unit) {
    val half = with(LocalDensity.current) { (size / 2).toPx() }
    Box(Modifier.offset { IntOffset((center.x - half).toInt(), (center.y - half).toInt()) }) { content() }
}

/** 핀 위 이름표 (흰 바탕 + 검정 테두리 알약). 가운데 정렬, 핀보다 위 */
@Composable
private fun PinLabel(anchor: Offset, text: String) {
    val lift = with(LocalDensity.current) { LabelLift.toPx() }
    CarDiaryText(
        text,
        style = CarDiaryTypography.LabelSmall.copy(fontWeight = CarDiaryTypography.BodySmallStrong.fontWeight),
        maxLines = 1,
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
                layout(0, 0) {
                    placeable.place((anchor.x - placeable.width / 2).toInt(), (anchor.y - lift - placeable.height).toInt())
                }
            }
            .background(CarDiaryColors.Card, CarDiaryShapes.Pill)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CarDiaryShapes.Pill)
            .padding(horizontal = 14.dp, vertical = 4.dp),
    )
}

/** 지도가 준비되기 전 자리: 위경도 → 화면 좌표(경도는 cos(위도)로 줄여 모양 유지), 가운데 맞춤 */
private fun projector(points: List<RouteMapPoint>, w: Float, h: Float, pad: Float): (RouteMapPoint) -> Offset {
    val minLat = points.minOf { it.latitude }
    val maxLat = points.maxOf { it.latitude }
    val minLon = points.minOf { it.longitude }
    val maxLon = points.maxOf { it.longitude }
    val k = cos(Math.toRadians((minLat + maxLat) / 2))
    val spanX = max((maxLon - minLon) * k, 1e-6)
    val spanY = max(maxLat - minLat, 1e-6)
    val scale = minOf((w - pad * 2) / spanX, (h - pad * 2) / spanY)
    val offX = (w - spanX * scale) / 2
    val offY = (h - spanY * scale) / 2
    return { p ->
        Offset(
            (offX + (p.longitude - minLon) * k * scale).toFloat(),
            (offY + (maxLat - p.latitude) * scale).toFloat(),
        )
    }
}

private val MapPadding = 72.dp
private val RouteOuter = 14.dp
private val RouteInner = 8.dp
private val StartSize = 30.dp
private val EndSize = 36.dp
private val HighlightSize = 56.dp
private val LabelLift = 26.dp
