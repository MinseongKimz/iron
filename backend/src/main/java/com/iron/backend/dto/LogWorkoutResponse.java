package com.iron.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogWorkoutResponse {
    private String parsedJson;
    private String feedback;
    private java.util.UUID sessionId;
}
