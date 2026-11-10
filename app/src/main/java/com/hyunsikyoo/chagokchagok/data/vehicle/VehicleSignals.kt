package com.hyunsikyoo.chagokchagok.data.vehicle

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.DoorArea
import ai.pleos.playground.vehicle.constant.area.DoorValue
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.listener.DoorPositionListener
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** 화면 전환에 쓰는 기어 상태 (P 정차 / D 주행) */
enum class GearState { Unknown, Park, Drive, Other }

/**
 * 화면 흐름(W0)에 필요한 차량 신호. Pleos Vehicle SDK로 받는다.
 * - 기어: Gear.registerGearSelection (퍼미션 CAR_DRIVE)
 * - 뒷문 열림: Door.registerDoorPosition ROW_2_LEFT/RIGHT (퍼미션 CAR_DOORS). 앞문만 열리면 탑승 팝업을 띄우지 않는다.
 * SDK 연결이 안 되면(지원 안 하는 차·퍼미션 없음) 조용히 무시하고, 시연 모드 버튼으로 같은 신호를 흉내 낸다.
 */
class VehicleSignals(context: Context) {

    private val vehicle = Vehicle(context.applicationContext)
    private val _gear = MutableStateFlow(GearState.Unknown)
    val gear: StateFlow<GearState> = _gear.asStateFlow()

    private val _rearDoorOpened = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val rearDoorOpened: SharedFlow<Unit> = _rearDoorOpened.asSharedFlow()

    private val gearListener = object : GearSelectionListener {
        override fun onSelected(gear: VehicleGear?) { _gear.value = gear?.toState() ?: GearState.Unknown }
        override fun onFailed(exception: Exception) { Log.w(TAG, "gear listener", exception) }
    }

    private val rearDoors = listOf(DoorArea(DoorValue.ROW_2_LEFT), DoorArea(DoorValue.ROW_2_RIGHT))
    private val lastDoorPosition = mutableMapOf<DoorArea, Int>()
    private val doorListeners = rearDoors.associateWith { area ->
        object : DoorPositionListener {
            override fun onPositionUpdated(position: Int?) {
                val now = position ?: 0
                val before = lastDoorPosition.put(area, now) ?: 0
                if (before == 0 && now > 0) _rearDoorOpened.tryEmit(Unit)
            }
            override fun onFailed(exception: Exception) { Log.w(TAG, "door listener", exception) }
        }
    }

    private var started = false

    fun start() {
        if (started) return
        started = true
        runCatching {
            vehicle.initialize()
            vehicle.getGear().getCurrentGearSelectionState(
                { _gear.value = it?.toState() ?: GearState.Unknown },
                { Log.w(TAG, "gear get", it) },
            )
            vehicle.getGear().registerGearSelection(gearListener)
            doorListeners.forEach { (area, listener) -> vehicle.getDoor().registerDoorPosition(area, listener) }
        }.onFailure { Log.w(TAG, "Vehicle SDK 연결 실패 — 시연 모드로만 동작", it) }
    }

    fun stop() {
        if (!started) return
        started = false
        runCatching {
            vehicle.getGear().unregisterGearSelection(gearListener)
            doorListeners.forEach { (area, listener) -> vehicle.getDoor().unregisterDoorPosition(area, listener) }
            vehicle.release()
        }
    }

    private fun VehicleGear.toState(): GearState = when (this) {
        VehicleGear.GEAR_PARK -> GearState.Park
        VehicleGear.GEAR_UNKNOWN -> GearState.Unknown
        VehicleGear.GEAR_NEUTRAL, VehicleGear.GEAR_REVERSE -> GearState.Other
        else -> GearState.Drive // D, 1~9단
    }

    private companion object {
        const val TAG = "VehicleSignals"
    }
}
