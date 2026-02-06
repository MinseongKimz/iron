package com.iron.backend.controller;

import com.iron.backend.dto.StatsDto;
import com.iron.backend.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StatsControllerTest {

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    @Test
    @DisplayName("대시보드 통계 조회 성공")
    public void getDashboardStats_ShouldReturnStats() {
        // Given
        Long userId = 1L;
        StatsDto mockStats = StatsDto.builder()
                .volumeByCategory(List.of(
                        new StatsDto.VolumeByCategory("가슴", 1000.0),
                        new StatsDto.VolumeByCategory("등", 2000.0)
                ))
                .build();

        given(statsService.getDashboardStats(userId)).willReturn(mockStats);

        // When
        StatsDto response = statsController.getDashboardStats(userId);

        // Then
        assertThat(response.getVolumeByCategory()).hasSize(2);
        assertThat(response.getVolumeByCategory().get(0).getCategory()).isEqualTo("가슴");
        assertThat(response.getVolumeByCategory().get(0).getTotalVolume()).isEqualTo(1000.0);
    }
}
