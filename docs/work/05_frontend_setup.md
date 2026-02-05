# 프론트엔드 초기 설정 (Frontend Setup)

## 1. 테마 및 스타일 (Theme & Style)
- **`globals.css` 구현**:
  - `Dark Mode`를 기본으로 하는 CSS 변수 설정.
  - **Color Palette**:
    - Primary: Neon Green (`#00E676`)
    - Background: Deep Black (`#121212`)
    - Surface: Lighter Black (`#1E1E1E`)
  - 모바일 중심의 Container 스타일 정의.

## 2. 레이아웃 (Layout)
- **`layout.tsx`**:
  - 전체 앱을 감싸는 루트 레이아웃.
  - `BottomNav` 컴포넌트를 하단에 고정 배치.
- **`BottomNav` 컴포넌트**:
  - 홈 / 기록 / 분석 3개의 메인 탭으로 구성.
  - 현재 경로(`usePathname`)에 따라 활성 탭 하이라이트 처리.

## 3. 현재 진행 상황
- [x] 프로젝트 초기화 (Next.js 14+).
- [x] 글로벌 스타일 및 테마 적용.
- [x] 모바일 레이아웃 및 하단 네비게이션 구현.
- [ ] 대시보드 화면 구현 예정.
