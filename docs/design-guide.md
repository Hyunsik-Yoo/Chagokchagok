# 차 일기 디자인 가이드 (싶 디자인 시스템)

차 일기는 [싶 디자인 시스템](https://claude.ai/artifact/Mhev8UMsdUZ6yEhfXfndUQ)을 그대로 쓴다. 새 화면을 그릴 때는 이 문서와 `screens/`의 시안을 기준으로, 싶 앱과 구분되지 않게 그린다. 색·서체·라운드는 `design/tokens.css`(싶 tokens.json과 같은 이름·값)에서만 고른다. 정말 필요하면 값을 만들지 말고 "새 토큰: 이름 = 값, 이유"를 따로 적는다.

## 화면 크기

- 차량 화면의 앱 영역(가로 1700×1184px, 세로 분할 840×1184px)에 **싶 pt를 2배로** 그린다. 화면 안에서는 싶 숫자를 그대로 쓴다.
  - 가로 = 850 × 592pt, 세로 분할 = 420 × 592pt (아이폰 폭과 비슷)
  - 개발: dp = pt × 2 (예: `radius-card` 12 → `RoundedCornerShape(24.dp)`, 헤드라인 22 → `44.sp`)
- 넓은 폭(600pt 초과)은 두 단으로 나누고, 좁은 폭(600pt 이하)은 한 단으로 쌓는다. 바텀시트는 넓은 폭에서도 최대 600pt.
- 다크 모드는 없다(싶: Light 고정).

## 콘텐츠

- 화면 문구는 **친근한 해요체**, 짧게. 예: "오늘 일기를 다 썼어요!", "다음에 할게요".
- **차가 쓰는 일기와 차가 묻는 말은 반말**(차의 목소리). 예: "오늘은 누구랑 가?", "여기 어디였어?", 일기 본문.
- 헤드라인은 두 줄: 1행 `title-light`, 2행 핵심만 `title-bold`. 숫자 강조는 `seep-blue`.
  - "11월 1일, 소나가 / **오늘 일기를 다 썼어요!**"
- **이모지는 장식이 아니라 데이터다.**
  - 가족 한 명 = 이모지 하나(👨 👩 👦 👧)
  - 일기 이벤트 하나 = 스탬프 이모지 하나(☔ 🌙 🍃 🛣️ ❄️ ☀️ ⭐ 🚩 ⛽)
  - 일기 문장 끝에 이모지 하나까지
  - 그 밖의 UI에는 쓰지 않는다. 예외로 토스트 앞에 👏 하나.
- 평가하는 말(급제동·연비 점수 등)은 쓰지 않는다. 항상 칭찬하거나 귀엽게.

## 색

- 바탕은 `gray2`, 카드는 `gray1`(그림자 없음). 회색 바탕 위에 흰 카드를 얹는다.
- 읽는 화면(그림일기)과 입력 화면(온보딩)은 바탕 자체를 `gray1`로 한다. 입력 상자는 `gray2`.
- 텍스트는 `gray5`(본문·제목), `gray4`(보조) 두 단계만. `gray3`는 아이콘·플레이스홀더·흐린 날짜에만.
- 브랜드 색은 두 개다. 같은 화면에서 역할을 바꿔 쓰지 않는다.
  - **`seep-blue` = 선택·탐색**: 선택된 탭·알약, 링크형 버튼, 강조 숫자
  - **`tennis-green` = 실행·완료**: CTA, 체크 켜짐, 진행 바 채움, 얻은 배지 링
- 고르기 알약(이름 추천, 자주 가는 곳)은 카테고리 트랙 색을 쓴다: 꺼짐 `category-track` + `category-text-off`, 켜짐 `seep-blue` + 흰 글자.
- 흰 글자는 굵은 14 이상의 탭, 17 CTA에만 쓴다(싶 접근성 규칙 그대로).

## 타이포

- 서체는 **Apple SD Gothic Neo** 하나(웹 미리보기는 Noto Sans KR). 굵기로만 위계를 만든다.
- 쓰는 크기만 쓴다(pt).

| 스타일 | 크기/줄 | 굵기 | 차 일기에서 |
| --- | --- | --- | --- |
| `title-light` · `title-bold` | 22/31 | 300 · 700 | 화면 헤드라인 |
| `doc-title` | 26/36 | 700 | 그림일기 제목 |
| `doc-dday` | 30/36 | 800 | 큰 숫자(오늘 km, 주행 중 km·분, 누적 거리) |
| `cta` | 17/24 | 800 | CTA |
| `wish-title` | 16/22 | 600 | 카드 제목, 가족 이름, 섹션 질문 |
| `body` · `doc-body` | 16/24 · 16/28 | 400 | 목록 줄 · 일기 본문 |
| `tab-on` · `label` · `body-sm` | 14/20 | 800 · 500 · 400 | 탭 · 보조 라벨 · 보조 본문 |
| `caption` | 12/17 | 500 | 카드 캡션, 시각, 진행 안내 |
| `badge` | 11/14 | 700 | 작은 표시("뒷좌석 감지") |

## 레이아웃 · 간격 · 라운드

- 화면 좌우 `space-page`(20), 헤드라인 왼쪽 `space-header`(22), 카드 사이 `space-list-gap`(10), 카드 안 `space-card`(14).
- 헤더: 헤드라인 왼쪽, 차 스탬프 오른쪽(싶 홈의 카테고리 일러스트 자리), 그 아래 줄에 카테고리 탭.
- 카드: `gray1`, `radius-card`(12), 그림자 없음.
- 카테고리 탭: 높이 30 알약(`radius-tab`).
  - 선택: `seep-blue` + 흰 `tab-on` + `shadow-tab`
  - 비선택: 바탕 없이 `gray4` `label`
  - 탭에 아이콘을 넣지 않는다.
- CTA(WriteButton): 높이 50, `radius-cta`, `tennis-green`, 흰 `cta`, 좌우 32. 한 화면에 하나. 비활성은 `cta-disabled`.
  - 되돌리는 쪽 버튼("이전")은 `gray2` + `gray5`
  - 물러나기는 `gray4` 텍스트 버튼("다음에 할게요")
- 작은 실행 버튼("맞아")은 탭 크기 초록 알약(높이 30).
- 링크형 버튼: ExtraBold 14 `seep-blue` + `ic_arrow_right` 16. 예: "다른 날 보기 ›".
- 진행 바(StepProgressBar): 트랙 `gray2-5`, 채움 `tennis-green`, 높이 4(큰 상자 8).
- 켜기·끄기, 고르기는 **체크 원**(`ic_check_on` / `ic_check_off`, 30). 스위치는 쓰지 않는다.
- 묻는 화면(탑승·도착)은 **바텀시트**(PanModal)로 쌓는다.
  - 모양: 위 라운드 20, 인디케이터
  - 순서: 헤드라인 → 내용 → CTA → 텍스트 버튼
- 토스트: `gray5` 바탕, 흰 Bold 15, `radius-toast`(8), 앞에 👏.
- 줄 목록: 높이 44, 사이 1pt `gray2-5` 구분선.
- 읽는 화면(그림일기)은 싶 **위시 상세 = 읽는 문서** 규칙을 따른다.
  - 흰 한 장에 카드·구분선 없이 쌓고, 섹션 사이는 40.
  - 순서: 스탬프 → `doc-title` 제목 → `doc-dday` 큰 숫자 + 날짜 `gray4` → 메타 한 줄(가족 · 날씨) → `doc-body` 본문 → 얇은 제목 섹션(명장면, 누적 거리)
  - 누를 수 있는 것만 `seep-blue`

## 스탬프 · 가족 · 아이콘

- 스탬프 = 싶 **배지 메달**: 원 + 링 3pt, 가운데 이모지.
  - 기본은 바탕 `gray2`, 링 `gray2-5`
  - 배지 모아보기에서 얻은 배지는 링 `tennis-green`, 바탕 `gray1`
  - 잠긴 배지는 메달만 불투명도 0.3, 이름은 `gray4`
- 크기: 30(목록) · 40(기본) · 56(배지·일기) · 72(큰 자리).
- 가족은 이모지 그대로. 여러 명은 `gray2` 원에 담아 겹쳐 놓는다.
- 가족 고르기는 싶 그리드 셀 모양이다.
  - 1줄 이모지, 2줄 이름 + 오른쪽 체크 원
  - 시트 안에서는 `gray2` 칸
- 아이콘은 싶 아이콘(`screens/assets/icons`, 원본 `assets/Icons` @3x)만 쓴다.
  - 쓰는 것: `ic_chevron_back`, `ic_arrow_right`, `ic_check_on`/`ic_check_off`, `ic_small_plus`, `ic_calendar`, `ic_close`
  - 새 아이콘이 필요하면 SF Symbols Regular에서 고르고 이름을 적는다. 직접 그리지 않는다.

## 화면 레퍼런스

| 파일 | 화면 | 쓰는 싶 컴포넌트 |
| --- | --- | --- |
| `screens/01-onboarding.html` | 온보딩 | InputField, 트랙 알약, 그리드 셀 + 체크, WriteButton |
| `screens/02-home.html` | 홈 | Headline, CategoryTabs, HomeCard, StepProgressBar |
| `screens/03-boarding.html` | 탑승 | 바텀시트, 그리드 셀 + 체크, StepProgressBar, WriteButton |
| `screens/04-driving.html` | 주행 중 | 탭 모양 상태 알약, `doc-dday` 숫자, 메달 |
| `screens/05-arrival.html` | 도착 | 바텀시트, 트랙 알약, 텍스트 버튼, Toast |
| `screens/06-diary.html` | 그림일기 | WishDetailDocument |
| `screens/07-diary-list.html` | 일기 목록 | CategoryTabs, HomeCard, 캘린더 칸 |
| `screens/08-milestone.html` | 함께 달린 거리 | CategoryTabs, 배지 모아보기 칸, StepProgressBar |
| `screens/09-special.html` | 특별한 날 | HomeCard, YearReviewCard, 올해의 한 줄 상자 |
| `screens/10-settings.html` | 설정 | 줄 목록 + 체크 원, 링크 화살표, 탭 알약 |
