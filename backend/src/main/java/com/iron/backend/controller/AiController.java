package com.iron.backend.controller;

import com.iron.backend.service.ai.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final com.iron.backend.service.WorkoutService workoutService;

    @PostMapping("/chat")
    public ResponseEntity<com.iron.backend.dto.LogWorkoutResponse> chat(@RequestBody com.iron.backend.dto.WorkoutLogDto logDto) {
        com.iron.backend.dto.LogWorkoutResponse result = workoutService.logWorkout(logDto);
        return ResponseEntity.ok(result);
    }
}
