package com.iron.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {
    private List<VolumeByCategory> volumeByCategory;
    private List<FrequencyByCategory> weeklyFrequency;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeByCategory {
        private String category;
        private Double totalVolume;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrequencyByCategory {
        private String category;
        private Long count; // Number of sets or sessions involved
    }
}
