import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hyunsikyoo.chagokchagok"
    compileSdk = 35

    // 릴리스 서명: 루트의 keystore.properties(저장소에 없음)가 있을 때만 쓴다. 포크한 사람은 debug 빌드로 실행하면 된다
    val keystoreProps = Properties().apply {
        val f = rootProject.file("keystore.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }
    signingConfigs {
        if (keystoreProps.isNotEmpty()) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "com.hyunsikyoo.chagokchagok"
        // Pleos Connect 요구사항: 사이닝 키 로테이션/리니지 지원을 위해 API 28 이상
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        // 시연 조작 패널·adb 시연 신호. 공모전 심사(에뮬레이터)에서 차 신호를 흉내 내야 해서 release에도 켠다
        buildConfigField("boolean", "DEMO_CONTROLS", "true")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfigs.findByName("release")?.let { signingConfig = it }
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    // 폰으로 보내기(W7) QR 코드 그리기
    implementation(libs.zxing.core)
    // 달린 길 지도 (OpenStreetMap 타일, 키 없음)
    implementation(libs.osmdroid.android)

    // Pleos Connect SDK — 사용하는 것만 주석 해제
    implementation(libs.pleos.vehicle)
    implementation(libs.pleos.navihelper)
    implementation(libs.pleos.tts)
    implementation(libs.pleos.stt)
    implementation(libs.pleos.fusedlocation)
}
