package com.iron.backend.repository;

import com.iron.backend.domain.workout.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {
    List<WorkoutSet> findByWorkoutSession_SessionId(UUID sessionId);
    
    // Find latest set for a specific exercise and user to compare
    WorkoutSet findTopByWorkoutSession_User_UserIdAndExercise_NameOrderByWorkoutSession_WorkoutDateDesc(Long userId, String exerciseName);
}
