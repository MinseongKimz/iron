package com.iron.backend.controller;

import com.iron.backend.dto.WorkoutLogDto;
import com.iron.backend.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping("/log")
    public ResponseEntity<com.iron.backend.dto.LogWorkoutResponse> logWorkout(@RequestBody WorkoutLogDto logDto) {
        return ResponseEntity.ok(workoutService.logWorkout(logDto));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> deleteWorkoutSession(@PathVariable java.util.UUID sessionId) {
        workoutService.deleteWorkoutSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sets")
    public ResponseEntity<Void> editWorkoutSets(@RequestBody com.iron.backend.dto.EditSetsRequest request) {
        workoutService.editWorkoutSets(request);
        return ResponseEntity.noContent().build();
    }
}
