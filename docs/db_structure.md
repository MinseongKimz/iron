# Iron Secretary (운동 비서) - Database Schema Specification

## 1. Overview
- **Database:** PostgreSQL (v13+)
- **Architecture:** Multi-tenancy (User-based separation)
- **Key Strategy:**
  - **UUID:** `workout_sessions`와 `workout_sets`는 대용량 데이터 분산 및 유니크성 보장을 위해 UUID 사용.
  - **AI Optimization:** `exercises` 테이블의 `synonyms` 컬럼(Array)을 통해 자연어 매핑 최적화.
  - **Superset Strategy:** 'Option 2' 채택 (데이터 구조는 단순하게 유지하고, 슈퍼세트 여부는 Session의 AI Feedback에 텍스트로 기록).

---

## 2. Entity Relationship Diagram (ERD) Summary

- **Users** (1) : (N) **BodySpecs**
- **Users** (1) : (N) **WorkoutSessions**
- **WorkoutSessions** (1) : (N) **WorkoutSets**
- **Exercises** (1) : (N) **WorkoutSets**

---

## 3. DDL (Data Definition Language)

### 3.1. Users (회원 및 인증)
회원의 기본 정보 및 현재 신체 스펙 캐싱.

```sql
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,      -- 로그인 ID
    password_hash VARCHAR(255) NOT NULL,     -- BCrypt Encoded Password
    nickname VARCHAR(50) NOT NULL,
    
    -- Current Body Spec (Caching for UI)
    current_weight NUMERIC(5, 2),            -- 현재 체중 (kg)
    target_weight NUMERIC(5, 2),             -- 목표 체중 (kg)
    
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### 3.2. Body Specs History (신체 스펙 이력)
체중, 골격근량 등의 변화 추이를 기록하는 시계열 데이터.
```sql
CREATE TABLE body_specs (
    spec_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    recorded_date DATE NOT NULL DEFAULT CURRENT_DATE,
    
    weight NUMERIC(5, 2),                    -- 체중 (kg)
    skeletal_muscle_mass NUMERIC(5, 2),      -- 골격근량 (kg)
    body_fat_percentage NUMERIC(4, 1),       -- 체지방률 (%)
    
    note TEXT,                               -- 비고 (예: "공복", "치팅데이 다음날")
    created_at TIMESTAMP DEFAULT NOW()
);

-- Index: 특정 회원의 체중 변화 그래프 조회용
CREATE INDEX idx_body_specs_user_date ON body_specs (user_id, recorded_date);
```

### 3.3. Exercises (운동 종목 사전 - Master Data)

전체 회원이 공유하는 운동 종목 메타 데이터.

```sql
CREATE TABLE exercises (
    exercise_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,       -- 정식 명칭 (예: Barbell Bench Press)
    
    -- Classification
    main_category VARCHAR(50) NOT NULL,      -- 대분류: CHEST, BACK, LEGS, SHOULDER, ARM, CORE
    sub_category VARCHAR(50),                -- 소분류: UPPER, LOWER, SIDE, WIDTH, THICKNESS
    
    -- AI Mapping Support
    synonyms TEXT[],                         -- 별칭 배열: ['벤치', '벤치 프레스', '가슴 미는거']
    
    -- Properties
    is_compound BOOLEAN DEFAULT FALSE,       -- 복합관절 운동 여부
    exercise_type VARCHAR(20) DEFAULT 'WEIGHT', -- WEIGHT, BODYWEIGHT, CARDIO
    
    -- (Optional) Custom Exercise Support
    created_by_user_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL
);

-- Index: AI 파싱 시 별칭 검색 가속 (GIN Index)
CREATE INDEX idx_exercises_synonyms ON exercises USING GIN (synonyms);
```

### 3.4. Workout Sessions (운동 세션 - Header)

하루 운동의 전체 요약 정보. user_id를 포함하여 데이터 격리.

```sql
CREATE TABLE workout_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    
    workout_date DATE NOT NULL DEFAULT CURRENT_DATE,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_minutes INTEGER,                -- 운동 소요 시간
    
    -- AI Integration
    raw_input TEXT,                          -- 사용자가 입력한 원본 텍스트 (로그용)
    ai_feedback_summary TEXT,                -- AI의 그날 운동 총평
    
    created_at TIMESTAMP DEFAULT NOW()
);

-- Index: 캘린더 뷰 및 날짜별 조회
CREATE INDEX idx_sessions_user_date ON workout_sessions (user_id, workout_date);
```

### 3.5. Workout Sets (운동 세트 - Detail)
실제 운동 수행 기록. 점진적 과부하 계산의 핵심 데이터.

```sql
CREATE TABLE workout_sets (
    set_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES workout_sessions(session_id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL REFERENCES exercises(exercise_id),
    
    set_order INTEGER NOT NULL,              -- 세트 순서 (1, 2, 3...)
    weight NUMERIC(5, 2) NOT NULL DEFAULT 0, -- 수행 무게
    reps INTEGER NOT NULL DEFAULT 0,         -- 수행 횟수
    
    -- Calculated Column (PostgreSQL 12+)
    volume NUMERIC(10, 2) GENERATED ALWAYS AS (weight * reps) STORED,
    
    -- Set Attributes
    is_warmup BOOLEAN DEFAULT FALSE,         -- 워밍업 여부 (1RM 계산 제외 등)
    rpe INTEGER,                             -- 자각도 (1~10)
    
    created_at TIMESTAMP DEFAULT NOW()
);

-- Index: 점진적 과부하 분석용 (특정 세션의 특정 종목 기록 조회)
CREATE INDEX idx_sets_session_exercise ON workout_sets (session_id, exercise_id);
```

## 4. Key Queries (Business Logic)

### 4.1. 점진적 과부하 체크 (Previous Record Comparison)

특정 유저가 특정 종목(예: 벤치프레스)을 수행했을 때, 과거 기록과 
비교.

```sql
SELECT 
    ws.workout_date,
    MAX(wset.weight) as max_weight,      -- 최고 중량
    SUM(wset.volume) as total_volume,    -- 총 볼륨
    COUNT(wset.set_id) as total_sets     -- 총 세트 수
FROM workout_sets wset
JOIN workout_sessions ws ON wset.session_id = ws.session_id
WHERE wset.exercise_id = :exerciseId
  AND ws.user_id = :userId
GROUP BY ws.workout_date
ORDER BY ws.workout_date DESC
LIMIT 5;
```

### 4.2. 부위별 운동 비중 분석 (AI Feedback Data)
최근 일주일간 어느 부위를 많이 했는지 조회.

```sql
SELECT 
    e.main_category,
    COUNT(DISTINCT ws.session_id) as frequency, -- 몇 번 했는지
    SUM(wset.volume) as total_volume            -- 볼륨 총합
FROM workout_sets wset
JOIN workout_sessions ws ON wset.session_id = ws.session_id
JOIN exercises e ON wset.exercise_id = e.exercise_id
WHERE ws.user_id = :userId
  AND ws.workout_date >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY e.main_category;
```