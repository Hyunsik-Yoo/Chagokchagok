package com.hyunsikyoo.pleosapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hyunsikyoo.pleosapp.data.vehicle.GearState
import com.hyunsikyoo.pleosapp.ui.catalog.DesignSystemCatalog
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTheme
import com.hyunsikyoo.pleosapp.ui.navigation.CarDiaryApp
import com.hyunsikyoo.pleosapp.ui.navigation.CarDiaryAppViewModel

/**
 * 앱 진입점.
 * - 디자인 시스템 카탈로그: `adb shell am start -n com.hyunsikyoo.pleosapp/.MainActivity --ez catalog true`
 * - 시연 신호(디버그 빌드만): `adb shell am broadcast -a com.hyunsikyoo.pleosapp.DEMO --es signal park|drive|door|end|nodest`
 */
class MainActivity : ComponentActivity() {

    private val appViewModel: CarDiaryAppViewModel by viewModels()

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
        setContent {
            CarDiaryTheme {
                if (showCatalog) {
                    DesignSystemCatalog()
                } else {
                    CarDiaryApp(onLeaveApp = { moveTaskToBack(true) }, app = appViewModel)
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
        const val ACTION_DEMO = "com.hyunsikyoo.pleosapp.DEMO"
    }
}
