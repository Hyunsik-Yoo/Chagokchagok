plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hyunsikyoo.pleosapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hyunsikyoo.pleosapp"
        // Pleos Connect 요구사항: 사이닝 키 로테이션/리니지 지원을 위해 API 28 이상
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    // Pleos Connect SDK — 사용하는 것만 주석 해제
    implementation(libs.pleos.vehicle)
    // implementation(libs.pleos.navihelper)
    // implementation(libs.pleos.tts)
    // implementation(libs.pleos.stt)
    // implementation(libs.pleos.fusedlocation)
}
