package com.iron.backend.repository;

import com.iron.backend.domain.workout.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {
    List<WorkoutSet> findByWorkoutSession_SessionId(UUID sessionId);
    
    // Find latest set for a specific exercise and user to compare
    WorkoutSet findTopByWorkoutSession_User_UserIdAndExercise_NameOrderByWorkoutSession_WorkoutDateDesc(Long userId, String exerciseName);

    @org.springframework.data.jpa.repository.Query("SELECT new com.iron.backend.dto.StatsDto$VolumeByCategory(e.mainCategory, SUM(ws.weight * ws.reps)) " +
           "FROM WorkoutSet ws " +
           "JOIN ws.exercise e " +
           "JOIN ws.workoutSession s " +
           "WHERE s.user.userId = :userId " +
           "GROUP BY e.mainCategory")
    List<com.iron.backend.dto.StatsDto.VolumeByCategory> findTotalVolumeByCategory(@org.springframework.data.repository.query.Param("userId") Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT new com.iron.backend.dto.StatsDto$FrequencyByCategory(e.mainCategory, COUNT(ws)) " +
            "FROM WorkoutSet ws " +
            "JOIN ws.exercise e " +
            "JOIN ws.workoutSession s " +
            "WHERE s.user.userId = :userId " +
            "AND s.workoutDate >= :startDate " +
            "GROUP BY e.mainCategory")
    List<com.iron.backend.dto.StatsDto.FrequencyByCategory> findWeeklyFrequencyByCategory(
            @org.springframework.data.repository.query.Param("userId") Long userId,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate
    );
}
