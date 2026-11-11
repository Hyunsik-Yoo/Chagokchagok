package com.hyunsikyoo.chagokchagok.data.location

import ai.pleos.playground.location.fused.FusedLocation
import ai.pleos.playground.location.fused.listener.FusedLocationListener
import ai.pleos.playground.location.fused.model.LocationData
import android.content.Context
import android.util.Log
import com.hyunsikyoo.chagokchagok.domain.model.RoutePoint
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/** 위치를 받을 수 있는지. 지원 안 함(차종)·막힘(차량 개인정보 설정)·권한 없음이면 경로 없이 일기를 쓴다 */
enum class LocationAvailability { Unknown, Available, Unsupported, Blocked }

/**
 * 이동 경로 기록 — Pleos FusedLocation SDK (퍼미션 pleos.car.permission.FUSED_LOCATION).
 * 트립 동안만 [start]~[stop]으로 위치를 받고, 점은 [points]로 흘려보낸다. 저장은 차 안에서만.
 * 업데이트 주기는 SDK 문서에 없어서, 너무 가까운 점(10m 안)은 버린다.
 */
class LocationTracker(context: Context) {

    private val fused = FusedLocation(context.applicationContext)

    private val _points = MutableSharedFlow<RoutePoint>(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val points: SharedFlow<RoutePoint> = _points.asSharedFlow()

    private val _availability = MutableStateFlow(LocationAvailability.Unknown)
    val availability: StateFlow<LocationAvailability> = _availability.asStateFlow()

    private var initialized = false
    private var tracking = false
    private var last: RoutePoint? = null

    private val listener = object : FusedLocationListener {
        override fun onChanged(data: LocationData?) {
            // 위치가 막히면 null이 온다
            if (data == null) {
                _availability.value = LocationAvailability.Blocked
                return
            }
            _availability.value = LocationAvailability.Available
            emit(data.toPoint())
        }

        override fun onError(exception: Exception) {
            // 차량 개인정보 보호 설정으로 막혔거나 권한이 없을 때
            Log.w(TAG, "location error", exception)
            _availability.value = LocationAvailability.Blocked
        }
    }

    fun start() {
        if (tracking) return
        runCatching {
            if (!initialized) {
                if (!fused.isSupported()) {
                    _availability.value = LocationAvailability.Unsupported
                    return
                }
                fused.initialize()
                initialized = true
            }
            last = null
            fused.registerFusedLocationCallback(listener)
            tracking = true
        }.onFailure {
            Log.w(TAG, "FusedLocation 연결 실패 — 경로 없이 기록", it)
            _availability.value = LocationAvailability.Unsupported
        }
    }

    fun stop() {
        if (!tracking) return
        tracking = false
        runCatching { fused.unregisterFusedLocationCallback(listener) }
    }

    fun release() {
        stop()
        if (initialized) runCatching { fused.release() }
        initialized = false
    }

    /** 위치가 막혀 있을 때 Pleos 시스템 안내 팝업 */
    fun showBlockedPopup() {
        runCatching { fused.showLocationAccessBlockedPopup() }
    }

    private fun emit(point: RoutePoint) {
        val prev = last
        if (prev != null && distanceMeters(prev, point) < MIN_STEP_METERS) return
        last = point
        _points.tryEmit(point)
    }

    private fun LocationData.toPoint() = RoutePoint(
        latitude = latitude,
        longitude = longitude,
        at = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault()),
    )

    companion object {
        private const val TAG = "LocationTracker"
        private const val MIN_STEP_METERS = 10.0

        /** 두 점 사이 거리(m), 하버사인 */
        fun distanceMeters(a: RoutePoint, b: RoutePoint): Double {
            val r = 6_371_000.0
            val dLat = Math.toRadians(b.latitude - a.latitude)
            val dLon = Math.toRadians(b.longitude - a.longitude)
            val h = Math.sin(dLat / 2).let { it * it } +
                Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) * Math.sin(dLon / 2).let { it * it }
            return 2 * r * Math.asin(Math.sqrt(h))
        }
    }
}
