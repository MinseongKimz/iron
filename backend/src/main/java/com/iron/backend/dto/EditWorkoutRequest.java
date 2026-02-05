package com.iron.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EditWorkoutRequest {
    private String rawInput;
    private LocalDate date;
}
