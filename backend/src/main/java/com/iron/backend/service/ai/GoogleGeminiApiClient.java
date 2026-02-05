package com.iron.backend.service.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoogleGeminiApiClient implements GeminiApiClient {

    @Override
    public String generateContent(String apiKey, String prompt) {
        // Mock for testing logic moved here or kept in Service?
        // Service handles High-level logic (like "dummy" check), Client handles low level.
        // But the original code had "dummy" check in Service. Let's keep it there for now or move key check here?
        // Let's keep "dummy" check in Service as it's a business rule for testing/dev mode.
        // This Client should just do the API call.

        try {
            Client client = Client.builder().apiKey(apiKey).build();
            
            GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash-lite", // Updated to latest stable or use config
                prompt,
                null
            );

            return response.text();
        } catch (Exception e) {
            log.error("Gemini API 호출 중 구글 클라이언트 오류 발생", e);
            throw new RuntimeException("Gemini API 호출 실패", e);
        }
    }
}
