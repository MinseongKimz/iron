package com.iron.backend.repository;

import com.iron.backend.domain.workout.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
    List<WorkoutSession> findByUser_UserIdAndWorkoutDate(Long userId, LocalDate workoutDate);
    List<WorkoutSession> findByUser_UserIdAndWorkoutDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
