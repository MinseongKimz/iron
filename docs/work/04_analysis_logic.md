# 분석 로직 구현 현황 (Analysis Implementation)

## 1. 개요
점진적 과부하(Progressive Overload)를 판단하기 위한 핵심 알고리즘을 구현합니다.

## 2. 주요 로직
### 1RM 추정 공식 (Epley Formula 변형)
$$ 1RM = Weight \times (1 + \frac{Reps}{30}) $$
- 이 공식을 통해 서로 다른 무게와 반복횟수의 수행 능력을 표준화하여 비교합니다.

### 과부하 판단
- **Volume**: (무게 x 횟수) 총합이 지난 세션보다 증가했는지 확인.
- **Intensity**: 최고 수행 중량이 증가했는지 확인.

## 3. 구현 클래스
- **`AnalysisService`**: 계산 로직 담당.
- **TODO**: DB에서 "해당 종목을 수행한 가장 최근의 기록"을 효율적으로 가져오는 쿼리 작성이 필요합니다.
