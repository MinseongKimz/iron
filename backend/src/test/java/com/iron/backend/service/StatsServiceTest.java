package com.iron.backend.service;

import com.iron.backend.dto.StatsDto;
import com.iron.backend.repository.WorkoutSetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("사용자의 운동 볼륨 및 주간 빈도 통계를 반환한다")
    public void getDashboardStats_Success() {
        // Given
        Long userId = 1L;
        List<StatsDto.VolumeByCategory> mockVolume = List.of(
                new StatsDto.VolumeByCategory("가슴", 500.0),
                new StatsDto.VolumeByCategory("하체", 1000.0)
        );
        List<StatsDto.FrequencyByCategory> mockFrequency = List.of(
                new StatsDto.FrequencyByCategory("가슴", 2L),
                new StatsDto.FrequencyByCategory("등", 1L)
        );

        given(workoutSetRepository.findTotalVolumeByCategory(userId)).willReturn(mockVolume);
        given(workoutSetRepository.findWeeklyFrequencyByCategory(eq(userId), any(java.time.LocalDate.class))).willReturn(mockFrequency);

        // When
        StatsDto result = statsService.getDashboardStats(userId);

        // Then
        assertThat(result.getVolumeByCategory()).hasSize(2);
        assertThat(result.getWeeklyFrequency()).hasSize(2);
        assertThat(result.getWeeklyFrequency()).extracting("category").contains("가슴", "등");
        assertThat(result.getWeeklyFrequency()).extracting("count").contains(2L, 1L);
    }
}
