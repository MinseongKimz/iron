# AI 연동 및 보안 가이드 (AI Integration & Security)

## 1. 구현 내용 (Implementation)
- **`GeminiService`**: Google Gemini 1.5 Flash 모델을 호출하여 자연어를 운동 데이터로 변환합니다.
- **`AiController`**: 테스트를 위한 간단한 진입점 (`POST /api/ai/chat`)을 제공합니다.

## 2. 보안 가이드 (Security Guide)
**중요**: API Key는 절대 소스 코드(`application.properties` 포함)에 직접 입력하여 Git에 올리면 안 됩니다.

### 안전한 API Key 관리 방법

#### 방법 A: 서버 환경 변수 (추천)
서버(또는 로컬 터미널)에서 환경 변수로 키를 주입합니다.
```bash
# 1. 터미널에서 프로필 파일 열기
nano ~/.bashrc  # 또는 ~/.zshrc

# 2. 맨 아래에 다음 줄 추가
export GEMINI_API_KEY="AIzaSy...당신의_실제_키"

# 3. 적용
source ~/.bashrc

# 4. 애플리케이션 실행
./gradlew bootRun
```
Spring Boot는 `${GEMINI_API_KEY}` 플레이스홀더를 통해 이 환경 변수를 자동으로 읽어옵니다.

#### 방법 B: 실행 시 인자로 전달
일회성으로 실행할 때 유용합니다.
```bash
./gradlew bootRun -Dgemini.api.key="AIzaSy...키"
```

#### 방법 C: Docker 실행 시 (배포 시)
```bash
docker run -e GEMINI_API_KEY="AIzaSy...키" -p 8080:8080 iron-backend
```

## 3. 테스트 방법
Postman 또는 Curl을 사용하여 테스트할 수 있습니다.
```bash
curl -X POST http://localhost:8080/api/ai/chat \
     -H "Content-Type: application/json" \
     -d '{"text": "벤치프레스 100kg 5회 3세트"}'
```
