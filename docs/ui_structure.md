# Iron Secretary (운동 비서) - UI/UX Specification

## 1. Design Concept & Guidelines
- **Theme:** `Dark Mode` Default (헬스장 환경 고려, 배터리 절약).
- **Color Palette:**
  - **Primary:** `Neon Green (#00E676)` (성장, 완료, 긍정 피드백)
  - **Secondary:** `Slate Gray (#64748B)` (보조 텍스트)
  - **Alert:** `Tomato Red (#FF453A)` (실패, 중량 감소)
  - **Background:** `Deep Black (#121212)`
- **Interaction:** 채팅형 인터페이스(Chat-UI) 중심의 경험.

---

## 2. Site Map (Bottom Navigation)
1.  **Dashboard (Home):** 현재 상태 요약, 주간 현황.
2.  **Iron Chat (Core):** 메모 입력, AI 파싱 및 피드백.
3.  **History (Log):** 캘린더, 상세 운동 기록 열람.
4.  **Stats (Growth):** 체중 및 볼륨 변화 그래프.

---

## 3. Detailed Screen Specifications

### 3.1. Dashboard (Home)
앱 진입 시 가장 먼저 보이는 화면.

**A. Header (Status)**
- **User Info:** 닉네임, 현재 날짜.
- **Body Spec:**
  - 현재 체중 vs 목표 체중 (Progress Bar).
  - "오늘 체중 기록하기" 버튼 (모달 팝업).

**B. Weekly Streak (잔디 심기)**
- 월~일요일 아이콘.
- 운동한 날은 Green Circle, 쉰 날은 Gray Dot.
- **Logic:** `workout_sessions` 테이블 조회.

**C. Today's Suggestion (AI 예측)**
- **Card:** "오늘은 [등] 운동 하는 날인가요?" (지난 기록 기반 추천).
- **Logic:** 최근 3일간 안 한 부위 노출.

**D. Quick Actions**
- [운동 기록 시작하기] (Iron Chat 탭으로 이동).

---

### 3.2. Iron Chat (The Core Feature)
사용자가 메모를 입력하고 결과를 확인하는 메인 화면.

**A. Chat Stream (Timeline)**
- **User Bubble:** 사용자가 입력한 원본 텍스트.
- **AI Processing:** "운동 데이터를 분석 중입니다..." (Skeleton UI).
- **AI Response Card (핵심):**
  - **Summary:** "등/이두 운동을 기록했습니다."
  - **Result Table:** 파싱된 운동 목록 (종목명 | 무게 | 횟수 | 세트).
  - **Comparison Badge:**
    - (▲ 200kg) : 지난번 대비 볼륨 상승.
    - (New PR) : 최고 중량 갱신 시 금색 테두리.
  - **Feedback Text:** "벤치프레스 중량이 지난번보다 줄었네요. 컨디션 난조인가요?"

**B. Input Area (Bottom Fixed)**
- **Textarea:** "오늘 운동 메모 입력..." (자동 높이 조절).
- **Attach Btn:** 사진 업로드 (추후 식단/오운완 인증용).
- **Send Btn:** 전송 아이콘.

---

### 3.3. Workout Detail (Editable View)
AI가 파싱한 결과 카드를 눌렀을 때 진입하는 상세/수정 화면.
*(AI가 '세트'를 '회'로 잘못 인식했을 때 수정하는 용도)*

**A. Session Header**
- 날짜, 총 소요시간, 총 볼륨.

**B. Exercise List (Accordion Style)**
- **Header:** 종목명 (예: 랫풀다운).
- **Content:**
  - Set 1: 40kg x 12reps [삭제 버튼]
  - Set 2: 45kg x 10reps
  - [+ 세트 추가 버튼]

**C. Save/Delete**
- [수정 완료] (DB 업데이트).
- [기록 삭제].

---

### 3.4. Stats & Analytics
점진적 과부하를 시각적으로 확인하는 화면.

**A. Weight Tracker**
- **Chart:** Line Chart (X축: 날짜, Y축: 체중).
- 목표 체중 가이드라인(Dotted Line) 표시.

**B. Volume Progression (성장 그래프)**
- **Filter:** 종목 선택 (Dropdown: 벤치프레스, 스쿼트, 데드...).
- **Chart:** Bar Chart (날짜별 1RM 또는 총 볼륨 변화).
- **Insight:** "최근 1달간 벤치프레스 1RM이 5kg 증가했습니다."

**C. Muscle Balance (Pie Chart)**
- 최근 30일간 수행한 대분류 비율 (등 40%, 가슴 30%, 하체 10%...).
- **Alert:** "하체 비중이 너무 낮습니다!" (경고 UI).

---

### 3.5. Profile & Settings
**A. My Info**
- 현재 신체 스펙 수정.
- 목표 설정 수정.

**B. Data Management**
- 운동 종목 커스텀 추가 (AI가 인식 못하는 종목 수동 등록).

---

## 4. Key Components (For Developer)
*재사용 가능한 UI 컴포넌트 목록*

1.  **`WorkoutCard`**: 채팅창에 뜨는 요약 카드.
2.  **`TrendArrow`**: 상승(Green ▲), 하락(Red ▼), 유지(Gray -) 아이콘.
3.  **`MuscleTag`**: [등-광배], [가슴-상부] 등 뱃지 스타일.
4.  **`QuickAddModal`**: 체중 입력 등 간단한 입력을 위한 하프 모달.

## 5. User Flow Example
1.  User opens app → Dashboard shows "82.8kg".
2.  User taps "Chat" tab.
3.  User pastes text: "2/2 등 운동 랫풀다운 40kg 10회 4셋..."
4.  App sends to Backend → AI parses.
5.  Screen displays **AI Response Card**:
    - "랫풀다운 4세트 인식됨"
    - "지난주보다 볼륨 +100kg 증가! (Good)"
6.  User checks graph in "Stats" tab to see monthly progress.