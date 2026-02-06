package com.iron.backend.repository;

import com.iron.backend.domain.workout.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
    List<WorkoutSession> findByUser_UserIdAndWorkoutDate(Long userId, LocalDate workoutDate);
    List<WorkoutSession> findByUser_UserIdAndWorkoutDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s.workoutDate FROM WorkoutSession s WHERE s.user.userId = :userId ORDER BY s.workoutDate DESC")
    List<LocalDate> findDistinctWorkoutDatesByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
