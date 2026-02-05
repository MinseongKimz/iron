package com.iron.backend.service;

import com.iron.backend.domain.exercise.Exercise;
import com.iron.backend.domain.workout.WorkoutSet;
import com.iron.backend.repository.WorkoutSessionRepository;
import com.iron.backend.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutSetRepository setRepository;

    public String analyzeProgressiveOverload(Long userId, String exerciseName, Double currentWeight, Integer currentReps) {
        // 1. Find the most recent previous set for this exercise
        WorkoutSet lastSet = setRepository.findTopByWorkoutSession_User_UserIdAndExercise_NameOrderByWorkoutSession_WorkoutDateDesc(userId, exerciseName);

        if (lastSet == null) {
            return exerciseName + ": 첫 기록이네요! 화이팅입니다.";
        }

        // Simple comparison: Max Weight or Estimated 1RM
        double current1RM = calculateOneRepMax(currentWeight, currentReps);
        double last1RM = calculateOneRepMax(lastSet.getWeight(), lastSet.getReps());

        String feedback = exerciseName + ": ";
        if (current1RM > last1RM) {
            feedback += String.format("지난번보다 1RM이 약 %.1fkg 증가했습니다! (%.1fkg -> %.1fkg) 성장하고 계시네요!", (current1RM - last1RM), last1RM, current1RM);
        } else if (current1RM < last1RM) {
             feedback += String.format("지난번보다 컨디션이 조금 저조하네요. (1RM %.1fkg -> %.1fkg) 꾸준함이 답입니다!", last1RM, current1RM);
        } else {
             feedback += "지난번과 비슷한 강도입니다. 다음엔 무게나 횟수를 조금 더 늘려보세요!";
        }
        
        return feedback;
    }

    // 1RM 추정 (Epley 공식)
    public double calculateOneRepMax(double weight, int reps) {
        if (reps == 1) return weight;
        return weight * (1 + (double)reps / 30.0);
    }
}
