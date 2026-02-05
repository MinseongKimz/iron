package com.iron.backend.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iron.backend.dto.AiWorkoutResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiApiClient geminiApiClient;
    private final ObjectMapper objectMapper;

    /**
     * 사용자의 운동 로그 텍스트를 분석하여 구조화된 데이터(AiWorkoutResult)로 반환합니다.
     *
     * @param apiKey  사용자의 Gemini API 키
     * @param rawText 사용자가 입력한 운동 로그 텍스트
     * @return 분석된 운동 결과 DTO
     */
    public AiWorkoutResult parseWorkoutLog(String apiKey, String rawText) {
        validateInput(apiKey, rawText);

        // 1. 테스트용 더미 응답 처리
        if (apiKey.startsWith("dummy")) {
            return getDummyData();
        }

        // 2. 프롬프트 생성
        String prompt = buildPrompt(rawText);

        // 3. API 호출 및 파싱
        try {
            String rawJson = geminiApiClient.generateContent(apiKey, prompt);
            return parseResponse(rawJson);
        } catch (RuntimeException e) {
            // 이미 처리된 예외는 그대로 던짐 (parseResponse 등에서 발생한 예외)
            if (e.getMessage().contains("AI 응답을 분석하는 도중")) {
                throw e;
            }
            // 그 외 API 호출 오류 등
            throw new RuntimeException("AI를 통한 운동 로그 분석 실패", e);
        }
    }

    private void validateInput(String apiKey, String rawText) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API 키가 제공되지 않았습니다.");
        }
        if (rawText == null || rawText.trim().isEmpty()) {
            throw new IllegalArgumentException("분석할 운동 로그가 없습니다.");
        }
    }

    private AiWorkoutResult parseResponse(String rawJson) {
        try {
            String cleanedJson = cleanJson(rawJson);
            return objectMapper.readValue(cleanedJson, AiWorkoutResult.class);
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패. Raw JSON: {}", rawJson, e);
            throw new RuntimeException("AI 응답을 분석하는 도중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 마크다운 코드 블록(```json ... ```)을 제거하고 순수 JSON 문자열만 추출합니다.
     */
    private String cleanJson(String jsonResponse) {
        if (jsonResponse == null) return "{}";
        
        String result = jsonResponse.trim();
        if (result.startsWith("```json")) {
            result = result.substring(7);
        } else if (result.startsWith("```")) {
            result = result.substring(3);
        }

        if (result.endsWith("```")) {
            result = result.substring(0, result.length() - 3);
        }
        
        return result.trim();
    }

    private String buildPrompt(String rawText) {
        return """
            Role: 헬스 데이터 표준화 전문가
            Goal: 사용자의 자연어 운동 로그를 분석하여 '표준 운동명'과 '정확한 카테고리'로 변환하시오.
            Input: "%s"

            Rules:
            1. **운동명 표준화 (가장 중요)**:
               - 사용자의 입력을 아래 '표준 데이터 사전'에 있는 명칭으로 매핑하시오. 사전에 없으면 가장 일반적인 한글 명칭을 사용하시오.
               - 영어 입력("Bench Press")이나 약어("벤치"), 구체적 수식어("플랫 벤치프레스")는 모두 '표준 명칭'("벤치프레스")으로 통일하시오.
               
               [표준 데이터 사전]
               - 가슴: 벤치프레스, 인클라인 벤치프레스, 덤벨 프레스, 인클라인 덤벨 프레스, 딥스, 케이블 크로스오버, 펙덱 플라이, 체스트 프레스
               - 등: 데드리프트, 렛 풀 다운, 시티드 로우, 벤트오버 바벨 로우, 원암 덤벨 로우, 풀업, 어시스트 풀업
               - 하체: 스쿼트, 레그 프레스, 레그 익스텐션, 레그 컬, 런지, 카프 레이즈, 힙 어브덕션, 이너 싸이
               - 어깨: 오버헤드 프레스(밀리터리 프레스), 덤벨 숄더 프레스, 사이드 레터럴 레이즈, 페이스 풀, 리어 델트 플라이
               - 팔: 바벨 컬, 덤벨 컬, 해머 컬, 트라이셉스 익스텐션, 케이블 푸쉬 다운
               
               * 예외: "스쿼트"는 기본적으로 "백 스쿼트"를 의미하지만 표준명은 "스쿼트"로 통일. "프론트 스쿼트"는 별도.

            2. **세트 정보 추출**:
               - 한 줄에 여러 세트 정보가 나열된 경우, 각각 별도의 세트 객체로 분리하시오.
               - "N셋", "N세트" 등의 표현이 있으면 해당 세트 객체를 N개 실제로 생성(복제)하시오.
               - 예: "20kg 10회 3세트" -> [{weight:20, reps:10}, {weight:20, reps:10}, {weight:20, reps:10}]
               - **무게 처리**: 
                 - "40.9kg(90lb)" 처럼 두 단위가 병기된 경우 명시된 kg 값을 우선하여 사용하시오.
                 - 만약 'lb', 'lbs' 단위만 주어진 경우, 반드시 kg으로 환산하여 저장하시오. (1 lb = 0.4536 kg, 소수점 첫째 자리까지 반올림)
               - '웜업' 키워드가 있으면 `isWarmup: true`.
               
            3. **카테고리 분류 규칙**:
               - **main_category**: 타겟 부위 (가슴, 등, 하체, 어깨, 팔, 유산소, 코어, 전신) 중 택1. "OTHER" 사용 지양.
               - **sub_category**: 운동 도구/방식 (바벨, 덤벨, 머신, 케이블, 맨몸, 이지바, 스미스머신) 중 택1. 특정 부위명(예: "가슴 전체")을 적지 말고 **도구** 위주로 분류하시오.

            4. **Feedback**:
               - 운동 내용에 기반한 1문장의 짧고 간결한 칭찬/응원 메시지. 분석 내용은 제외.

            Output JSON Schema:
            {
              "workout_date": "YYYY-MM-DD",
              "exercises": [
                { 
                  "name": "Standard Name", 
                  "main_category": "Target Part",
                  "sub_category": "Equipment Type",
                  "sets": [
                    { "weight": 0.0, "reps": 0, "isWarmup": boolean }
                  ] 
                }
              ],
              "feedback": "Encouragement message"
            }
            오직 JSON만 반환하시오.
            """.formatted(rawText);
    }

    // 테스트를 위한 더미 데이터 반환
    private AiWorkoutResult getDummyData() {
        try {
            String dummyJson = """
                {
                  "workout_date": "2026-01-31",
                  "exercises": [
                    { 
                      "name": "벤치프레스", 
                      "main_category": "가슴",
                      "sub_category": "프리웨이트",
                      "sets": [
                        { "weight": 60, "reps": 10, "isWarmup": false },
                        { "weight": 60, "reps": 10, "isWarmup": false },
                        { "weight": 60, "reps": 10, "isWarmup": false },
                        { "weight": 60, "reps": 10, "isWarmup": false },
                        { "weight": 60, "reps": 10, "isWarmup": false }
                      ] 
                    },
                    { 
                      "name": "인클라인 덤벨 프레스", 
                      "main_category": "가슴",
                      "sub_category": "덤벨",
                      "sets": [
                        { "weight": 20, "reps": 10, "isWarmup": false },
                        { "weight": 20, "reps": 10, "isWarmup": false },
                        { "weight": 20, "reps": 10, "isWarmup": false },
                        { "weight": 20, "reps": 10, "isWarmup": false }
                      ] 
                    },
                    { 
                      "name": "딥스", 
                      "main_category": "가슴",
                      "sub_category": "맨몸",
                      "sets": [
                        { "weight": 0, "reps": 10, "isWarmup": false },
                        { "weight": 0, "reps": 10, "isWarmup": false },
                        { "weight": 0, "reps": 10, "isWarmup": false },
                        { "weight": 0, "reps": 10, "isWarmup": false }
                      ] 
                    },
                    { 
                      "name": "케이블 크로스오버", 
                      "main_category": "가슴",
                      "sub_category": "케이블",
                      "sets": [
                        { "weight": 15, "reps": 12, "isWarmup": false },
                        { "weight": 15, "reps": 12, "isWarmup": false },
                        { "weight": 15, "reps": 12, "isWarmup": false },
                        { "weight": 15, "reps": 12, "isWarmup": false }
                      ] 
                    }
                  ],
                  "feedback": "테스트 데이터: 오늘은 가슴을 찢으셨군요! 대단합니다."
                }
                """;
            return objectMapper.readValue(dummyJson, AiWorkoutResult.class);
        } catch (Exception e) {
            throw new RuntimeException("더미 데이터 파싱 실패", e);
        }
    }
}

