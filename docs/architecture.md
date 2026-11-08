# 차 일기 앱 구조

기준: 팀 시안 아티팩트 「차 일기 Pleos 화면 시안」의 **디자인 완료** 페이지(시안·핸드오프·디자인 시스템)와 **와이어프레임** 페이지(정책 원본, W0~W9).

## 진행 순서

1. **구조 + 디자인 시스템** ← 완료 (2026-11-07)
2. **디자인 완료 화면 먼저 구현**: 일기 화면(와이어프레임 C) ← 완료 (2026-11-07, 시연 데이터), 주행 중 화면(W1)
   - 일기 화면: `feature/diary` (요약 줄, 일기 종이·종이 줄·스티커, 들려주기 형광펜, 명장면·조각·누적 카드, 기록 전체 팝업, 조각 전체 팝업)
   - 아직 시연용: 데이터는 `data/demo/DemoDiaryRepository`, 들려주기는 소리 없는 `TimedDemoNarrator`(Pleos TTS는 앱 등록 후 연결)
   - 주행 중 화면(W1) ← 완료: `feature/driving` (기록 중 알약 깜빡임, 상태 문장 3종, 출발→목적지, 지금까지 모인 명장면 최신 위 3줄 + 로딩 점, 기록 끄기 오늘만)
3. **디자인이 없는 화면은 와이어프레임대로 구현** ← 완료 (2026-11-07): W2 홈 `feature/home`, W4 달력 `feature/calendar`, W9 설정 `feature/settings`, 팝업 W3 탑승·W6 도착·W7 폰으로 보내기(QR)·W8 특별한 날 `feature/popups`, 명장면 전체 팝업 `feature/diary`
   - 화면 흐름(W0): `ui/navigation/CarDiaryAppViewModel` — 기어 P→D면 어디서든 주행 중으로(탑승 팝업은 확정, 도착 팝업은 "나중에"), D→P 후 트립 종료(10분, 시연 모드 5초)면 도착 팝업 → 일기, 뒷문 열림 + 정차면 탑승 팝업, 도착 팝업 30초 자동 저장
   - 차량 신호: `data/vehicle/VehicleSignals` — Vehicle SDK Gear(`registerGearSelection`, CAR_DRIVE)·Door(`registerDoorPosition` 뒷문 2개, CAR_DOORS). Gear는 문서 도메인 목록엔 없지만 SDK 2.3.2에 있다
   - 시연: 디버그 빌드 `adb shell am broadcast -a com.hyunsikyoo.pleosapp.DEMO --es signal door|drive|park|end|nodest` (nodest = 다음 트립을 내비 목적지 없이 → W6-1)
   - 아직 시연 값: 내비 목적지·ETA(NaviHelper), 좌석 착좌로 탑승 덮어쓰기(Seat), 위치·근처 후보(FusedLocation·위키백과), 실제 명장면 감지·일기 문장 생성, 저장(Room)
4. **디자인이 완료되면 해당 화면에만 디자인을 입힌다** ← 완료 (2026-11-08, 시안 「차 일기 Pleos 화면 시안」 디자인 완료 페이지)
   - 디자인 시스템: 회색을 검정 투명도로 교체, Button Small(72), Checkbox·Text Field·Toast 추가, List Row Checkbox/아이콘 줄, Header Mix, Popup 뒤로 가기·높이 자동, Seat Card 나이 태그, Calendar Day Short 삭제·Today 추가
   - W2 홈(+W2-1 기록 준비됨·Toast·소나의 한마디), W3 탑승(+W3-0 처음·W3-1 자리 고르기·W3-2 새 가족 추가), W1-1 기록 꺼짐, W4 달력, W6 도착(+W6-1 목적지 없음·W6-2 이름 붙이기·W6-3 실패), W7 QR(+W7-1 만료), W8 특별한 날, W9 설정 4줄(+W9-1 차 이름·W9-2 가족 고치기), 일기 W-D1 들려주는 중·W-D2 명장면 전체보기·W-D3 조각 전체보기
   - 범위 제외(11/8): W5 마일스톤 화면 삭제, 설정의 일기 숨기기·타임캡슐·목소리·시연 모드 줄 삭제(시연 모드는 adb 신호로만)
   - 가족 모델: 관계 + 생일(나이 계산). 시연 "오늘" = 2026-11-08

## 패키지

```
com.hyunsikyoo.pleosapp
├─ MainActivity                 진입점 (지금은 디자인 시스템 카탈로그)
├─ ui/
│  ├─ designsystem/
│  │  ├─ foundation/            토큰: Color, Typography(Asta Sans), Dimens·Shapes·Shadows, Modifiers(hardShadow·dashedBorder), Theme
│  │  ├─ icon/                  CarDiaryIcons — Feather 26종 (SVG에서 생성, 손으로 고치지 않음)
│  │  └─ component/             컴포넌트 22개 + 앱 틀(CarDiaryAppFrame)
│  ├─ catalog/                  디자인 시스템 카탈로그 (개발 확인용)
│  └─ navigation/               (2단계) 화면 전환. 기어 P/D 자동 전환 규칙(W0)
├─ feature/                     (2~3단계) 화면별 패키지: diary, driving, home, boarding, calendar, milestone, arrival, sendphone, specialday, settings
├─ domain/                      (2단계) Trip·Event·Highlight·Piece·Diary·Mileage, 명장면 선정·일기 문장 규칙
└─ data/                        (2단계) Pleos SDK 연결(vehicle, navi, location, tts), 로컬 DB(Room), 시연 모드 데이터
```

- 화면 코드는 `ui/designsystem`만 쓰고 Material 컴포넌트는 쓰지 않는다.
- 차량 데이터는 `data`가 Pleos SDK로 받아 `domain` 모델로 바꾼다. 화면은 SDK를 직접 부르지 않는다.

## 디자인 시스템

| 구분 | 내용 | 파일 |
| --- | --- | --- |
| 화면 틀 | 앱 창 1700×1184, 바탕 #99D6FF, 안쪽 여백 40, 헤더 110 | `Dimens.kt`, `AppFrame.kt` |
| 색 | 앱 토큰 + Neobrutalism 팔레트 17색 | `Color.kt` |
| 글자 | Pleos 텍스트 스타일 (Asta Sans 가변 글꼴), 주행 중 상태 문장 72, 일기 본문 28/52 | `Typography.kt` |
| 선·모서리·그림자 | 테두리 검정 2px, 하드 섀도 6/4/8 (흐림 0), 버튼 눌림 4px 내려앉기 | `Dimens.kt`, `Modifiers.kt` |
| 아이콘 | Feather 26종, 32px, 선 2.67 | `icon/CarDiaryIcons.kt` |
| 컴포넌트 22개 | Header, Icon Button, Button, Text Button, Tag, Route Point, Avatar, Section Header, Highlight Row, Piece Tag, Card, Status Pill, Tab Chip, Toggle, Progress Bar, Banner, List Row, Seat Card, Calendar Day, Milestone Card, Popup, Icon | `component/` |

- 단위: 시안 px = dp. Pleos 에뮬레이터(2560×1440, 160dpi)는 1dp = 1px이다.
- Highlight Row·Calendar Day의 이모지는 지금 글자 이모지로 그린다. Fluent 3D PNG 매핑 표가 확정되면 `icon` 이미지로 바꾼다.
- Popup 버튼 색: 명세 글은 "Neutral 닫기 → Primary 확인"이고, Figma 그림은 닫기가 파랑(Secondary)이다. 명세 글을 따르고, 스타일은 `PopupAction.style`로 바꿀 수 있다.

## 라이선스

- Asta Sans: SIL OFL 1.1 → `docs/licenses/AstaSans-OFL.txt`
- Feather Icons 4.29.2: MIT → `docs/licenses/FeatherIcons-MIT.txt`
