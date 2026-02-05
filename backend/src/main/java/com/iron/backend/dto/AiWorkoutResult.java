package com.iron.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiWorkoutResult {
    private String workout_date;
    private List<AiExercise> exercises;
    private String feedback; // Added missing field

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiExercise {
        @com.fasterxml.jackson.annotation.JsonProperty("name")
        private String name;
        
        @com.fasterxml.jackson.annotation.JsonProperty("main_category")
        private String main_category; 
        
        @com.fasterxml.jackson.annotation.JsonProperty("sub_category")
        private String sub_category;
        
        @com.fasterxml.jackson.annotation.JsonProperty("sets")
        private List<AiSet> sets;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiSet {
        private Double weight;
        private Integer reps;
        private Boolean isWarmup;
    }
}
