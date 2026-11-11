# 차곡차곡 (Chagokchagok) — 현대자동차그룹 차량용 앱 개발 공모전

가족 차가 1인칭으로 그날의 드라이브를 일기로 남겨 주는 Pleos Connect 앱입니다. 뒷자리 아이와 함께 다닌 길을 차가 차곡차곡 기억합니다.

- 공모전: https://appdevchallenge.auto-hmg.io/ (접수 ~2026.11.15)
- 개발 문서: https://document.pleos.ai/docs/pleos-only/connect-sdk-pleos/
- 구조와 진행 상황: [docs/architecture.md](docs/architecture.md)

## 포크해서 사용하기

1. 이 저장소를 포크한 뒤 내려받습니다.

   ```bash
   git clone https://github.com/<내 계정>/Chagokchagok.git
   cd Chagokchagok
   ```

2. **`local.properties`를 만들고 값을 채웁니다.** 이 파일은 컴퓨터마다 달라서 저장소에 없고, 값을 비운 견본 `local.properties.example`만 있습니다.

   ```bash
   cp local.properties.example local.properties
   ```

   `local.properties`를 열어 `sdk.dir=` 뒤에 내 Android SDK 경로를 넣어 주세요. 경로는 Android Studio → Settings → Languages & Frameworks → Android SDK 맨 위 **Android SDK Location**에서 확인할 수 있습니다.

   | 값 | 설명 | 예 |
   |---|---|---|
   | `sdk.dir` | Android SDK 설치 경로 (필수) | macOS `/Users/<사용자이름>/Library/Android/sdk`<br>Windows `C\:\\Users\\<사용자이름>\\AppData\\Local\\Android\\Sdk` |

   Android Studio로 프로젝트를 처음 열면 이 파일을 자동으로 만들어 주기도 합니다. 그때는 값이 맞는지만 확인하면 됩니다.

3. JDK 17이 설치되어 있는지 확인합니다 (Android Studio 내장 JDK를 써도 됩니다).
4. 아래 **Pleos Connect Emulator 설치**를 마친 뒤 **빌드 & 실행**으로 앱을 띄웁니다.

Pleos Connect SDK는 로그인 없이 받는 공개 저장소(`nexus-playground.pleos.ai`)에서 내려받으므로 따로 넣을 계정 정보는 없습니다.

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

앱 아이디는 `com.hyunsikyoo.chagokchagok`입니다. 포크해서 따로 배포하려면 `app/build.gradle.kts`의 `applicationId`를 내 것으로 바꿔 주세요.

## 시연하기 (디버그 빌드)

에뮬레이터에서는 실제로 차를 움직일 수 없어서, 차 신호를 흉내 내는 방법 두 가지를 넣어 두었습니다. 둘 다 디버그 빌드에서만 동작합니다.

- **시연 조작 패널:** 앱 화면 왼쪽 아래 구석을 1초쯤 길게 누르면 열립니다. 뒷문 열림 · 주행 D · 주차 P · 트립 끝내기 · 다음 트립 목적지 없이 버튼이 있습니다.
- **adb 신호:** 같은 동작을 터미널에서 보냅니다. `signal` 값은 `door`, `drive`, `park`, `end`, `nodest` 중 하나입니다.

  ```bash
  adb shell am broadcast -a com.hyunsikyoo.chagokchagok.DEMO --es signal drive
  ```

디자인 시스템 컴포넌트를 한 화면에서 보려면:

```bash
adb shell am start -n com.hyunsikyoo.chagokchagok/.MainActivity --ez catalog true
```

일기·명장면 데이터는 아직 시연용 고정 데이터(`data/demo/DemoDiaryRepository`)이고, 들려주기는 소리 없이 형광펜만 움직입니다.

## 참고

- 차량 퍼미션은 `pleos.car.permission.*` 형식으로 `AndroidManifest.xml`에 선언. 사용하는 도메인 것만, App Market 승인 범위 내에서.
- Vehicle SDK 사용 흐름: `Vehicle(context).initialize()` → `getXxx()` 도메인 객체 → Capability/Get/Set/Callback → `release()`
- 서드파티 라이선스: Asta Sans(SIL OFL 1.1), Feather Icons(MIT), Fluent Emoji(MIT) → `docs/licenses/`
- 지도: osmdroid(Apache 2.0) + OpenStreetMap 타일(© OpenStreetMap contributors, ODbL). 공개 타일 서버는 대량 사용이 금지라 시연용으로만 쓰고, 출시 땐 타일 서버를 따로 둔다
