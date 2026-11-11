package com.hyunsikyoo.chagokchagok

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hyunsikyoo.chagokchagok.data.vehicle.GearState
import com.hyunsikyoo.chagokchagok.ui.catalog.DesignSystemCatalog
import com.hyunsikyoo.chagokchagok.ui.designsystem.foundation.CarDiaryTheme
import com.hyunsikyoo.chagokchagok.ui.navigation.CarDiaryApp
import com.hyunsikyoo.chagokchagok.ui.navigation.CarDiaryAppViewModel

/**
 * 앱 진입점.
 * - 디자인 시스템 카탈로그: `adb shell am start -n com.hyunsikyoo.chagokchagok/.MainActivity --ez catalog true`
 * - 시연 신호(디버그 빌드만): `adb shell am broadcast -a com.hyunsikyoo.chagokchagok.DEMO --es signal park|drive|door|end|nodest`
 * - 시연 조작 패널(디버그 빌드만): 앱 영역 왼쪽 아래 구석을 길게 누르기. 위 신호와 같은 동작
 */
class MainActivity : ComponentActivity() {

    private val appViewModel: CarDiaryAppViewModel by viewModels()

    /** 이동 경로 기록 권한. 거절하면 경로 없이 일기를 쓴다 */
    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val demoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("signal")) {
                "drive" -> appViewModel.onGear(GearState.Drive)
                "park" -> appViewModel.onGear(GearState.Park)
                "door" -> appViewModel.onRearDoorOpened()
                "end" -> appViewModel.endTripNow()
                "nodest" -> appViewModel.demoNextTripWithoutDestination()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val showCatalog = intent.getBooleanExtra(EXTRA_CATALOG, false)
        val debuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (debuggable) {
            ContextCompat.registerReceiver(this, demoReceiver, IntentFilter(ACTION_DEMO), ContextCompat.RECEIVER_EXPORTED)
        }
        if (ContextCompat.checkSelfPermission(this, PERMISSION_FUSED_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission.launch(PERMISSION_FUSED_LOCATION)
        }
        setContent {
            CarDiaryTheme {
                if (showCatalog) {
                    DesignSystemCatalog()
                } else {
                    CarDiaryApp(onLeaveApp = { moveTaskToBack(true) }, demoControls = debuggable, app = appViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(demoReceiver) }
        super.onDestroy()
    }

    companion object {
        const val EXTRA_CATALOG = "catalog"
        const val ACTION_DEMO = "com.hyunsikyoo.chagokchagok.DEMO"
        const val PERMISSION_FUSED_LOCATION = "pleos.car.permission.FUSED_LOCATION"
    }
}
