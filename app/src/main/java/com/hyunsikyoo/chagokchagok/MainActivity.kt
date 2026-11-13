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
 * - 시연 신호(debug 빌드와 심사용 release 빌드): `adb shell am broadcast -a com.hyunsikyoo.chagokchagok.DEMO --es signal park|drive|door|end|nodest`
 * - 시연 조작 패널(debug 빌드와 심사용 release 빌드): 앱 영역 왼쪽 아래 구석을 길게 누르기. 위 신호와 같은 동작
 */
class MainActivity : ComponentActivity() {

    private val appViewModel: CarDiaryAppViewModel by viewModels()

    /** Pleos 권한(위치·내비·TTS·STT). 위험 권한만 시스템 창이 뜬다. 거절하면 그 기능 없이 동작한다 */
    private val pleosPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

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
        // 시연 조작: debug 빌드, 또는 BuildConfig.DEMO_CONTROLS(공모전 심사용 release)
        val debuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 || BuildConfig.DEMO_CONTROLS
        if (debuggable) {
            ContextCompat.registerReceiver(this, demoReceiver, IntentFilter(ACTION_DEMO), ContextCompat.RECEIVER_EXPORTED)
        }
        val missing = PLEOS_PERMISSIONS.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) pleosPermissions.launch(missing.toTypedArray())
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
        val PLEOS_PERMISSIONS = listOf(
            "pleos.car.permission.FUSED_LOCATION",
            "pleos.car.permission.NAVI_ROUTE",
            "pleos.car.permission.TTS_SERVICE",
            "pleos.car.permission.STT_SERVICE",
        )
    }
}
