package com.hyunsikyoo.chagokchagok.data.navi

import ai.pleos.playground.navi.data.DestinationArrivedInfo
import ai.pleos.playground.navi.data.DestinationInfo
import ai.pleos.playground.navi.data.RouteStartInfo
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

/** 내비 목적지 (이름·주소·도착 예정 시각) */
data class NaviDestination(val name: String, val address: String?, val eta: LocalDateTime?)

/**
 * 내비 목적지·도착 — Pleos NaviHelper SDK (퍼미션 NAVI_ROUTE).
 * - 경로 안내가 시작되거나 목적지가 바뀌면 목적지를 다시 받아 [destination]에 둔다. 안내가 끝나면 null.
 * - 목적지에 닿으면 [arrived]로 알린다 → 트립 종료를 10분 기다리지 않는다(W0 "내비 목적지에 닿으면 바로 종료").
 * SDK를 못 쓰면 조용히 무시하고 시연 값으로 동작한다.
 */
class NaviSignals(context: Context) {

    private val navi = NaviHelper(context.applicationContext)

    private val _destination = MutableStateFlow<NaviDestination?>(null)
    val destination: StateFlow<NaviDestination?> = _destination.asStateFlow()

    private val _arrived = MutableSharedFlow<String?>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val arrived: SharedFlow<String?> = _arrived.asSharedFlow()

    private var started = false

    private val listener = object : NaviHelperEventListener {
        override fun onRouteStarted(info: RouteStartInfo) {
            refresh()
        }
        override fun onDestinationChanged() {
            refresh()
        }
        override fun onRouteEnded() {
            _destination.value = null
        }
        override fun onRouteCancelled() {
            _destination.value = null
        }
        override fun onDestinationInfo(info: DestinationInfo) {
            val name = info.name?.trim().orEmpty()
            _destination.value = if (name.isEmpty()) null else NaviDestination(
                name = name,
                address = info.address,
                // 남은 시간 단위는 문서에 없어 초로 본다
                eta = info.remainTime.takeIf { it > 0 }?.let { LocalDateTime.now().plusSeconds(it.toLong()) },
            )
        }
        override fun onDestinationArrived(info: DestinationArrivedInfo) {
            _arrived.tryEmit(info.name)
        }
    }

    fun start() {
        if (started) return
        started = true
        runCatching {
            navi.initialize(
                {
                    runCatching {
                        navi.addListener(listener)
                        navi.getDestinationInfo()
                    }.onFailure { Log.w(TAG, "navi setup", it) }
                },
                { Log.w(TAG, "NaviHelper 연결 실패 — 시연 값으로", it) },
            )
        }.onFailure { Log.w(TAG, "NaviHelper 연결 실패 — 시연 값으로", it) }
    }

    /** 지금 목적지를 다시 묻는다 (결과는 콜백으로) */
    fun refresh() {
        runCatching { navi.getDestinationInfo() }
    }

    fun stop() {
        if (!started) return
        started = false
        runCatching {
            navi.removeListener(listener)
            navi.release()
        }
    }

    private companion object {
        const val TAG = "NaviSignals"
    }
}
