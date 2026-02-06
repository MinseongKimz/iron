package com.iron.backend.service;

import com.iron.backend.domain.user.User;
import com.iron.backend.domain.workout.WorkoutSession;
import com.iron.backend.repository.UserRepository;
import com.iron.backend.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password", "TestUser");
        testUser.setUserId(1L);
    }

    @Test
    @DisplayName("연속 3일 운동 시 스트릭이 3이 되어야 함")
    void testStreakCalculation_ThreeConsecutiveDays() {
        // Given: 2월 3, 4, 5일에 운동 기록
        LocalDate referenceDate = LocalDate.of(2026, 2, 5);
        List<LocalDate> workoutDates = Arrays.asList(
            LocalDate.of(2026, 2, 5),
            LocalDate.of(2026, 2, 4),
            LocalDate.of(2026, 2, 3)
        );

        when(workoutSessionRepository.findDistinctWorkoutDatesByUserId(1L))
            .thenReturn(workoutDates);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When: 스트릭 재계산
        workoutService.recalculateUserStreak(1L, referenceDate);

        // Then: 스트릭이 3으로 업데이트되어야 함
        verify(userRepository).save(testUser);
        assertEquals(3, testUser.getCurrentStreak());
    }

    @Test
    @DisplayName("운동 날짜가 어제까지면 스트릭 유지")
    void testStreakCalculation_LastWorkoutYesterday() {
        // Given: 마지막 운동이 어제 (2/4), 오늘은 2/5
        LocalDate referenceDate = LocalDate.of(2026, 2, 5);
        List<LocalDate> workoutDates = Arrays.asList(
            LocalDate.of(2026, 2, 4),
            LocalDate.of(2026, 2, 3),
            LocalDate.of(2026, 2, 2)
        );

        when(workoutSessionRepository.findDistinctWorkoutDatesByUserId(1L))
            .thenReturn(workoutDates);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When: 스트릭 재계산
        workoutService.recalculateUserStreak(1L, referenceDate);

        // Then: 스트릭은 유지되어야 함 (3일)
        verify(userRepository).save(testUser);
        assertEquals(3, testUser.getCurrentStreak());
    }

    @Test
    @DisplayName("운동 날짜가 이틀 전이면 스트릭 0")
    void testStreakCalculation_LastWorkoutTwoDaysAgo() {
        // Given: 마지막 운동이 이틀 전 (2/3), 오늘은 2/5
        LocalDate referenceDate = LocalDate.of(2026, 2, 5);
        List<LocalDate> workoutDates = Arrays.asList(
            LocalDate.of(2026, 2, 3),
            LocalDate.of(2026, 2, 2),
            LocalDate.of(2026, 2, 1)
        );

        when(workoutSessionRepository.findDistinctWorkoutDatesByUserId(1L))
            .thenReturn(workoutDates);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When: 스트릭 재계산
        workoutService.recalculateUserStreak(1L, referenceDate);

        // Then: 스트릭은 0이 되어야 함
        verify(userRepository).save(testUser);
        assertEquals(0, testUser.getCurrentStreak());
    }

    @Test
    @DisplayName("비연속적인 운동 날짜는 마지막 연속 기간만 계산")
    void testStreakCalculation_NonConsecutiveDays() {
        // Given: 2/5, 2/4, 2/2, 2/1 (2/3이 빠짐)
        LocalDate referenceDate = LocalDate.of(2026, 2, 5);
        List<LocalDate> workoutDates = Arrays.asList(
            LocalDate.of(2026, 2, 5),
            LocalDate.of(2026, 2, 4),
            LocalDate.of(2026, 2, 2),
            LocalDate.of(2026, 2, 1)
        );

        when(workoutSessionRepository.findDistinctWorkoutDatesByUserId(1L))
            .thenReturn(workoutDates);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When: 스트릭 재계산
        workoutService.recalculateUserStreak(1L, referenceDate);

        // Then: 스트릭은 2 (2/5, 2/4)가 되어야 함
        verify(userRepository).save(testUser);
        assertEquals(2, testUser.getCurrentStreak());
    }
}
