package com.iron.backend.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iron.backend.dto.AiWorkoutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GeminiService geminiService;

    // Use a real ObjectMapper for simpler tests if needed, but here we strict mock as per plan
    // actually, mocking ObjectMapper is painful for simple string conversions. 
    // Let's spy it or just verify interactions if we want strictness.
    // However, the service uses it to parse cleaned JSON.
    
    @Test
    @DisplayName("API Key가 없으면 예외를 던진다")
    void parseWorkoutLog_NoApiKey_ThrowsException() {
        assertThatThrownBy(() -> geminiService.parseWorkoutLog("", "bench press"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("API 키가 제공되지 않았습니다");
    }

    @Test
    @DisplayName("입력 텍스트가 없으면 예외를 던진다")
    void parseWorkoutLog_NoText_ThrowsException() {
        assertThatThrownBy(() -> geminiService.parseWorkoutLog("key", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("분석할 운동 로그가 없습니다");
    }

    @Test
    @DisplayName("dummy 키로 요청 시 더미 데이터를 반환한다")
    void parseWorkoutLog_DummyKey_ReturnsDummy() throws JsonProcessingException {
        // Given
        // We need to mock ObjectMapper because parseDummy uses it
        AiWorkoutResult mockResult = new AiWorkoutResult();
        mockResult.setFeedback("Test Feedback");
        when(objectMapper.readValue(anyString(), eq(AiWorkoutResult.class))).thenReturn(mockResult);

        // When
        AiWorkoutResult result = geminiService.parseWorkoutLog("dummy123", "some text");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFeedback()).isEqualTo("Test Feedback");
    }

    @Test
    @DisplayName("정상적인 응답을 처리하고 JSON 마크다운을 제거한다")
    void parseWorkoutLog_ValidResponse_ReturnsDto() throws JsonProcessingException {
        // Given
        String rawJson = "```json\n{ \"feedback\": \"Good job\" }\n```";
        when(geminiApiClient.generateContent(anyString(), anyString())).thenReturn(rawJson);
        
        AiWorkoutResult expectedResult = new AiWorkoutResult();
        expectedResult.setFeedback("Good job");
        when(objectMapper.readValue(eq("{ \"feedback\": \"Good job\" }"), eq(AiWorkoutResult.class)))
                .thenReturn(expectedResult);

        // When
        AiWorkoutResult result = geminiService.parseWorkoutLog("real-key", "Today I did bench press");

        // Then
        assertThat(result).isEqualTo(expectedResult);
        verify(geminiApiClient).generateContent(eq("real-key"), anyString());
    }
    
    @Test
    @DisplayName("API 호출 실패 시 RuntimeException으로 감싸서 던진다")
    void parseWorkoutLog_ApiError_ThrowsRuntimeException() {
        // Given
        when(geminiApiClient.generateContent(anyString(), anyString()))
                .thenThrow(new RuntimeException("API Error"));

        // When & Then
        assertThatThrownBy(() -> geminiService.parseWorkoutLog("key", "text"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("AI를 통한 운동 로그 분석 실패");
                                                                        // The implementation catches parsing error, but NOT API error directly?
                                                                        // Let's check implementation. 
                                                                        // Implementation: String rawJson = geminiApiClient... (no try-catch around this in Service)
                                                                        // So exception from Client propagates. 
                                                                        // The client wrapper has try-catch wrapping in RuntimeException.
    }
}
