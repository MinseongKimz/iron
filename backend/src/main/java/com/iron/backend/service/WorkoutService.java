package com.iron.backend.service;

import com.iron.backend.dto.WorkoutLogDto;
import com.iron.backend.domain.exercise.Exercise;
import com.iron.backend.domain.workout.WorkoutSet;
import com.iron.backend.domain.workout.WorkoutSet;
import com.iron.backend.repository.WorkoutSessionRepository;
import com.iron.backend.repository.WorkoutSetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WorkoutService {

    private final com.iron.backend.service.ai.GeminiService geminiService;
    private final AnalysisService analysisService;
    private final ExerciseService exerciseService;
    private final com.iron.backend.repository.UserRepository userRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final ObjectMapper objectMapper;



    @Transactional
    public com.iron.backend.dto.LogWorkoutResponse logWorkout(WorkoutLogDto logDto) {
        // 0. User Fetch first to get API Key
        com.iron.backend.domain.user.User user = userRepository.findById(logDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getGeminiApiKey() == null || user.getGeminiApiKey().isEmpty()) {
             throw new IllegalStateException("Gemini API Key is missing for this user. Please login again.");
        }

        // 1. 날짜 처리 (입력 없으면 오늘 - KST 기준)
        java.time.LocalDate workoutDate = logDto.getDate();
        if (workoutDate == null) {
            workoutDate = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        }

                            // 2. Gemini를 통해 텍스트 분석 (User's API Key)
        // Refactored: GeminiService now returns DTO directly
        com.iron.backend.dto.AiWorkoutResult parsedResult;
        try {
             parsedResult = geminiService.parseWorkoutLog(user.getGeminiApiKey(), logDto.getRawInput());
        } catch (Exception e) {
             // Handle or rethrow? Service already throws RuntimeException
             throw e;
        }
        
        log.info("Gemini Parsed Result: {}", parsedResult);
        
        try {
            // JSON cleaning/parsing logic removed as it's handled in GeminiService now.
            
            String jsonResponse = ""; // Used for response
            try {
                jsonResponse = objectMapper.writeValueAsString(parsedResult);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                 jsonResponse = "{}"; // Fallback
            }

            // 3. Logic-based Analysis (Progressive Overload)
            StringBuilder feedbackBuilder = new StringBuilder();
            
            // Workout Session Create
            com.iron.backend.domain.workout.WorkoutSession session = new com.iron.backend.domain.workout.WorkoutSession(user, workoutDate);
            session.setRawInput(logDto.getRawInput());
            workoutSessionRepository.save(session); // Save first to get ID if needed, though Cascade might handle sets

            // Exercises & Sets Persistence & Analysis
            if (parsedResult.getExercises() != null) {
                for (com.iron.backend.dto.AiWorkoutResult.AiExercise aiExercise : parsedResult.getExercises()) {
                    // Refactored: Use ExerciseService
                    Exercise exercise = exerciseService.findOrCreateExercise(
                        aiExercise.getName(), 
                        aiExercise.getMain_category(), 
                        aiExercise.getSub_category()
                    );
                    
                    // Sets Create
                    int setOrder = 1;
                    double maxWeight = 0;
                    int maxReps = 0;

                    if (aiExercise.getSets() != null) {
                        for (com.iron.backend.dto.AiWorkoutResult.AiSet aiSet : aiExercise.getSets()) {
                            WorkoutSet workoutSet = new WorkoutSet();
                            workoutSet.setWorkoutSession(session);
                            workoutSet.setExercise(exercise);
                            workoutSet.setWeight(aiSet.getWeight());
                            workoutSet.setReps(aiSet.getReps());
                            workoutSet.setSetOrder(setOrder++);
                            workoutSet.calculateVolume(); 
                            workoutSetRepository.save(workoutSet);

                            // Track max for analysis
                            if (aiSet.getWeight() > maxWeight) {
                                maxWeight = aiSet.getWeight();
                                maxReps = aiSet.getReps(); // Reps at max weight
                            }
                        }
                    }

            // ... (Inside logWorkout) ...
            
            // Analyze Overload (Compare this new session vs previous) but keep feedback concise
            String analysis = analysisService.analyzeProgressiveOverload(user.getUserId(), exercise.getName(), maxWeight, maxReps);
            if (analysis != null && !analysis.isEmpty()) {
                // Only append if it's significant (This logic can be refined in AnalysisService, but for now we append)
                // However, user complained about length. Let's make it more compact or rely on UI to hide it.
                // For now, we will NOT append analysis to the 'Summary' feedback to keep it short as requested.
                // The analysis text is already saved? No, we need to save it somewhere if we want to show it.
                // Re-decision: User wants "Simple Cheer". Gemini provides that.
                // usage: session.setAiFeedbackSummary(geminiCheer);
                // We can separate analysis structure if needed later. For now, let's ONLY show the detailed analysis if it's a PR.
                // But the 'feedbackBuilder' was aggregating ALL exercises.
                // Let's just IGNORE the feedbackBuilder for the main summary and use Gemini's response.
                // feedbackBuilder.append(analysis).append("\n"); // Commented out to satisfy "Keep it simple"
            }
        }
    }
    
    // Use Gemini's feedback as the primary source for the "Short Message"
    String aiFeedback = parsedResult.getFeedback();
    if (aiFeedback == null || aiFeedback.isEmpty()) aiFeedback = "운동이 기록되었습니다. 고생하셨습니다!";
    
    session.setAiFeedbackSummary(aiFeedback);
    // ...
    
    return new com.iron.backend.dto.LogWorkoutResponse(jsonResponse, aiFeedback, session.getSessionId());
    
    } catch (Exception e) {
        throw new RuntimeException("운동 데이터 처리 중 오류 발생", e);
    }
} 

    @Transactional
    public void deleteWorkoutSession(UUID sessionId) {
        // Find session first
        com.iron.backend.domain.workout.WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Workout session not found"));
        
        // Delete all sets manually first
        workoutSetRepository.deleteAll(session.getWorkoutSets());
        
        // Then delete session
        workoutSessionRepository.delete(session);
    }

    @Transactional
    public void editWorkoutSets(com.iron.backend.dto.EditSetsRequest request) {
        // 1. Edits
        if (request.getSetEdits() != null) {
            for (com.iron.backend.dto.EditSetsRequest.SetEdit setEdit : request.getSetEdits()) {
                WorkoutSet workoutSet = workoutSetRepository.findById(setEdit.getSetId())
                        .orElseThrow(() -> new IllegalArgumentException("Workout set not found"));
                
                if (setEdit.getWeight() != null) workoutSet.setWeight(setEdit.getWeight());
                if (setEdit.getReps() != null) workoutSet.setReps(setEdit.getReps());
                
                workoutSet.calculateVolume();
                workoutSetRepository.save(workoutSet);
            }
        }

        // 2. Deletes
        if (request.getDeleteSetIds() != null && !request.getDeleteSetIds().isEmpty()) {
            workoutSetRepository.deleteAllById(request.getDeleteSetIds());
        }

        // 3. Adds
        if (request.getNewSets() != null) {
            for (com.iron.backend.dto.EditSetsRequest.NewSet newSet : request.getNewSets()) {
                com.iron.backend.domain.workout.WorkoutSession session = workoutSessionRepository.findById(newSet.getSessionId())
                        .orElseThrow(() -> new IllegalArgumentException("Session not found for new set"));
                
                Exercise exercise = exerciseService.findOrCreateExercise(newSet.getExerciseName(), null, null);

                WorkoutSet workoutSet = new WorkoutSet();
                workoutSet.setWorkoutSession(session);
                workoutSet.setExercise(exercise);
                workoutSet.setWeight(newSet.getWeight() != null ? newSet.getWeight() : 0);
                workoutSet.setReps(newSet.getReps() != null ? newSet.getReps() : 0);
                workoutSet.setSetOrder(newSet.getSetOrder() != null ? newSet.getSetOrder() : 99); // Default order
                workoutSet.calculateVolume();
                
                workoutSetRepository.save(workoutSet);
            }
        }
    }
}
