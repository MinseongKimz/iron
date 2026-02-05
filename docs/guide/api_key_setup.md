# API 키 설정 가이드

보안을 위해 API 키는 소스 코드에 포함하지 않고, **환경 변수(Environment Variable)**로 관리해야 합니다.

## 1. 리눅스/맥(Linux/Mac) 환경 설정

터미널에서 다음 명령어를 실행하여 환경 변수를 설정할 수 있습니다.

### 영구 설정 (추천)
`~/.bashrc` 또는 `~/.zshrc` 파일에 키를 등록하면 터미널을 열 때마다 자동으로 적용됩니다.

1. 파일 열기:
   ```bash
   nano ~/.bashrc
   ```

2. 맨 아래에 다음 내용 추가:
   ```bash
   export GEMINI_API_KEY="여기에_당신의_AI_키를_입력하세요"
   ```
   *(따옴표 안의 내용을 실제 발급받은 키로 교체하세요)*

3. 저장하고 닫기 (`Ctrl+O`, `Enter`, `Ctrl+X`)

4. 변경 사항 적용:
   ```bash
   source ~/.bashrc
   ```

### 일회성 설정 (테스트용)
```bash
export GEMINI_API_KEY="여기에_당신의_AI_키를_입력하세요"
```

## 2. 애플리케이션 실행

환경 변수가 설정된 상태에서 애플리케이션을 실행하면 자동으로 키를 인식합니다.

```bash
cd backend
./gradlew bootRun
```

## 3. Docker 사용 시

Docker로 실행할 때는 `-e` 옵션으로 키를 전달합니다.

```bash
docker run -e GEMINI_API_KEY="여기에_당신의_AI_키를_입력하세요" ...
```
