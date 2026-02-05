package com.iron.backend.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username; // treating 'test1' as username/email
    private String apiKey;   // Gemini API Key
}
