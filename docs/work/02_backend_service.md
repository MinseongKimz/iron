# 백엔드 서비스 레이어 구현 현황

## 개요
이 문서는 백엔드의 비즈니스 로직을 담당하는 Service Layer와 데이터 전송을 위한 DTO(Data Transfer Object) 구현 내역을 기술합니다.

## 1. DTO (Data Transfer Objects)
API 통신 및 계층 간 데이터 이동을 위해 다음 DTO들을 생성했습니다.
- **`UserDto`**: 회원 가입 및 프로필 조회 시 사용되는 객체. 민감 정보(비밀번호 등)는 제외하고 닉네임, 체중 정보 등을 포함합니다.
- **`ExerciseDto`**: 운동 종목 검색 결과 반환용 객체. 종목명, 카테고리, 유의어(Synonyms) 리스트를 포함합니다.
- **`WorkoutLogDto`**: 사용자가 채팅으로 입력한 자연어 운동 기록을 전달받는 객체입니다.

## 2. Services (Business Logic)
- **`UserService`**
  - `registerUser`: 회원 가입 로직 (비밀번호 암호화는 추후 구현 예정).
  - `getUserProfile`: 사용자 ID로 상세 프로필 정보를 조회.
- **`ExerciseService`**
  - `getAllExercises`: 등록된 모든 운동 종목을 조회하여 DTO로 변환 반환.
- **`WorkoutService`**
  - `logWorkout`: 사용자의 자연어 입력을 받아 AI 파싱 및 저장을 처리하는 핵심 메소드 (현재 뼈대만 구현됨).

## 향후 계획 (Next Steps)
- **AI 연동**: `WorkoutService` 내부에서 Gemini API를 호출하여 텍스트를 구조화된 데이터로 변환하는 로직 구현.
- **Security**: Spring Security를 도입하여 인증/인가 및 패스워드 암호화 적용.
