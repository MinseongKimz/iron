package com.iron.backend.domain.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkoutSetTest {

    @Test
    @DisplayName("무게와 횟수가 주어지면 볼륨이 계산된다")
    void calculateVolume_Success() {
        // Given
        WorkoutSet set = new WorkoutSet();
        set.setWeight(100.0);
        set.setReps(5);

        // When
        set.calculateVolume();

        // Then
        assertThat(set.getVolume()).isEqualTo(500.0);
    }

    @Test
    @DisplayName("무게나 횟수가 null이면 볼륨은 0이다")
    void calculateVolume_NullValues() {
        // Given
        WorkoutSet set = new WorkoutSet();
        set.setWeight(null);
        set.setReps(10);

        // When
        set.calculateVolume();

        // Then
        assertThat(set.getVolume()).isEqualTo(0.0);
    }
}
