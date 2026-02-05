package com.iron.backend.service.ai;

public interface GeminiApiClient {
    String generateContent(String apiKey, String prompt);
}
