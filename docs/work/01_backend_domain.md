# 백엔드 도메인 구현 현황 (Backend Domain Status)

## 개요
이 문서는 백엔드 도메인 계층 (Entities & Repositories)의 구현 내역을 기술합니다.

## 1. 회원 도메인 (User Domain)
- **User (`users`)**
  - 인증(이메일/비밀번호) 및 프로필 관리.
  - 현재 시점의 신체 스펙(체중, 목표 체중)을 캐싱하여 화면에 빠르게 표시.
- **BodySpec (`body_specs`)**
  - 신체 변화 기록(체중, 골격근량, 체지방률)을 저장하는 시계열 데이터.
  - User와 1:N 관계.

## 2. 운동 종목 도메인 (Exercise Domain)
- **Exercise (`exercises`)**
  - 운동 종목 마스터 데이터 (예: 벤치프레스, 스쿼트).
  - 유의어(`synonyms`) 처리를 위해 `@ElementCollection`으로 별칭 목록 저장.
  - 대분류(Main Category) 및 소분류(Sub Category) 지원.

## 3. 운동 기록 도메인 (Workout Domain)
- **WorkoutSession (`workout_sessions`)**
  - 하루 운동 세션을 나타내는 헤더 정보.
  - UUID를 PK로 사용.
  - 원본 입력 텍스트(`raw_input`)와 AI 분석 요약(`ai_feedback_summary`) 저장.
- **WorkoutSet (`workout_sets`)**
  - 실제 수행한 운동 세트 상세 정보 (무게, 횟수, 볼륨).
  - 볼륨(`weight * reps`) 자동 계산 로직 포함 (`@PrePersist`, `@PreUpdate`).

## 현재 상태 (Current Status)
- [x] 모든 Entity 클래스 구현 완료.
- [x] 기본 JpaRepository 인터페이스 생성 완료.
