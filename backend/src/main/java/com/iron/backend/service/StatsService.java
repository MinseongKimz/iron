package com.iron.backend.service;

import com.iron.backend.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final com.iron.backend.repository.WorkoutSetRepository workoutSetRepository;

    public StatsDto getDashboardStats(Long userId) {
        java.time.LocalDate oneWeekAgo = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).minusDays(7);
        return StatsDto.builder()
            .volumeByCategory(workoutSetRepository.findTotalVolumeByCategory(userId))
            .weeklyFrequency(workoutSetRepository.findWeeklyFrequencyByCategory(userId, oneWeekAgo))
            .build();
    }
}
