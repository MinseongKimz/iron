package com.iron.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogDto {
    private Long userId;
    private String rawInput;
    private java.time.LocalDate date; // Optional: if null, use today
    // Will include more fields for response later
}
