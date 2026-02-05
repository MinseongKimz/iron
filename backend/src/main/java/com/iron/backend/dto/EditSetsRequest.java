package com.iron.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class EditSetsRequest {
    private List<SetEdit> setEdits; // Existing sets to update
    private List<java.util.UUID> deleteSetIds; // Sets to delete
    private List<NewSet> newSets; // New sets to add

    @Data
    public static class SetEdit {
        private java.util.UUID setId;
        private Double weight;
        private Integer reps;
    }

    @Data
    public static class NewSet {
        private java.util.UUID sessionId; // Which session to add to
        private String exerciseName;      // Which exercise (find existing or create)
        private Double weight;
        private Integer reps;
        private Integer setOrder;
    }
}
