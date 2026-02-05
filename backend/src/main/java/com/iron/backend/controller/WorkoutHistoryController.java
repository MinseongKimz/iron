package com.iron.backend.controller;

import com.iron.backend.domain.workout.WorkoutSession;
import com.iron.backend.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workout/history")
@RequiredArgsConstructor
public class WorkoutHistoryController {

    private final WorkoutSessionRepository sessionRepository;

    // Monthly Status (Return list of dates that have workouts)
    // GET /api/workout/history/monthly?year=2024&month=2
    @GetMapping("/monthly")
    public ResponseEntity<List<String>> getMonthlyStatus(
            @RequestParam int year, 
            @RequestParam int month,
            @RequestParam Long userId) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<WorkoutSession> sessions = sessionRepository.findByUser_UserIdAndWorkoutDateBetween(userId, startDate, endDate);

        List<String> workoutDates = sessions.stream()
                .map(session -> session.getWorkoutDate().toString())
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(workoutDates);
    }

    // Daily Details
    // GET /api/workout/history/{date}
    @GetMapping("/{date}")
    public ResponseEntity<List<DailyWorkoutResponse>> getDailyDetails(
            @PathVariable String date,
            @RequestParam Long userId) {
        LocalDate workoutDate = LocalDate.parse(date);

        List<WorkoutSession> sessions = sessionRepository.findByUser_UserIdAndWorkoutDate(userId, workoutDate);
        
        List<DailyWorkoutResponse> response = sessions.stream().map(session -> {
            DailyWorkoutResponse dto = new DailyWorkoutResponse();
            dto.setSessionId(session.getSessionId());
            dto.setSummary(session.getAiFeedbackSummary());
            dto.setRawInput(session.getRawInput());
            
            // Group sets by exercise
            Map<String, List<com.iron.backend.domain.workout.WorkoutSet>> setsByExercise = session.getWorkoutSets().stream()
                .collect(Collectors.groupingBy(s -> s.getExercise().getName()));
            
            List<HistoryExerciseDto> exercises = setsByExercise.entrySet().stream().map(entry -> {
                HistoryExerciseDto exDto = new HistoryExerciseDto();
                exDto.setName(entry.getKey());
                
                List<HistorySetDto> setDtos = entry.getValue().stream()
                    .sorted(java.util.Comparator.comparingInt(com.iron.backend.domain.workout.WorkoutSet::getSetOrder))
                    .map(s -> {
                        HistorySetDto sDto = new HistorySetDto();
                        sDto.setSetId(s.getSetId()); // Include setId for editing
                        sDto.setSetOrder(s.getSetOrder());
                        sDto.setWeight(s.getWeight());
                        sDto.setReps(s.getReps());
                        return sDto;
                    }).collect(Collectors.toList());
                    
                exDto.setSets(setDtos);
                return exDto;
            }).collect(Collectors.toList());
            
            dto.setExercises(exercises);
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @lombok.Data
    static class DailyWorkoutResponse {
        private java.util.UUID sessionId;
        private String summary;
        private String rawInput;
        private List<HistoryExerciseDto> exercises;
    }

    @lombok.Data
    static class HistoryExerciseDto {
        private String name;
        private List<HistorySetDto> sets;
    }

    @lombok.Data
    static class HistorySetDto {
        private java.util.UUID setId; // Added for editing
        private Integer setOrder;
        private Double weight;
        private Integer reps;
    }
}
