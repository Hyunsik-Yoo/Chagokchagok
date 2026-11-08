# Pleos App — 현대자동차그룹 차량용 앱 개발 공모전

- 공모전: https://appdevchallenge.auto-hmg.io/ (접수 ~2026.11.15)
- 개발 문서: https://document.pleos.ai/docs/pleos-only/connect-sdk-pleos/

## 개발 환경

| 항목 | 버전 |
|---|---|
| JDK | 17 |
| Gradle wrapper | 8.13 |
| Android Gradle Plugin | 8.8.2 |
| Kotlin | 2.0.0 (Compose) |
| compileSdk / targetSdk | 35 |
| minSdk | 28 (Pleos 요구사항) |
| Pleos Connect Emulator | 2.1.0 (API 34) |

Pleos Connect SDK 버전은 `gradle/libs.versions.toml`에서 관리합니다.
Vehicle 2.3.2 / NaviHelper 3.0.5 / TextToSpeech 3.3.1 / SpeechToText 3.3.1 / FusedLocation 1.1.3
(`app/build.gradle.kts`에서 필요한 SDK만 주석 해제)

## Pleos Connect Emulator 설치 (최초 1회)

1. Android Studio → **Tools > SDK Manager > SDK Update Sites** → `+`
   - Name: `Pleos Connect System Image`
   - URL (Mac): `https://nexus-playground.pleos.ai/repository/raw-releases/connect-avd/2.x/crp-arm64-sys-img.xml`
2. **SDK Platforms** 탭 → *Show Package Details* 체크 → Android 14 (API 34) 아래 **Pleos Connect system image** 체크 → OK (라이선스 동의) → 설치 후 Studio 재시작
3. **Tools > Device Manager > Create device** → Form Factor **Automotive** → **Pleos Connect v2.1.0**
   - Advanced Settings: Default boot **Cold**, RAM **4 GB**
   - System image: API 34 → **Pleos Connect v2.1.0 APIs** → Finish
4. AVD 실행

## 빌드 & 실행

```bash
./gradlew assembleDebug
./gradlew installDebug   # 에뮬레이터 실행 중일 때
```

앱을 실행하면 Vehicle SDK로 VIN을 조회해 화면에 표시합니다 (SDK 연결 확인용).

## 참고

- 차량 퍼미션은 `pleos.car.permission.*` 형식으로 `AndroidManifest.xml`에 선언. 사용하는 도메인 것만, App Market 승인 범위 내에서.
- Vehicle SDK 사용 흐름: `Vehicle(context).initialize()` → `getXxx()` 도메인 객체 → Capability/Get/Set/Callback → `release()`
