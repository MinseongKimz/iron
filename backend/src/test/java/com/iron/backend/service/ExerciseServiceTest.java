package com.iron.backend.service;

import com.iron.backend.domain.exercise.Exercise;
import com.iron.backend.repository.ExerciseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    @DisplayName("운동이 이미 존재하면 해당 운동을 반환한다")
    void findOrCreateExercise_Exists_ReturnsExercise() {
        // Given
        Exercise existing = new Exercise("pushup", "chest");
        when(exerciseRepository.findByName("pushup")).thenReturn(Optional.of(existing));

        // When
        Exercise result = exerciseService.findOrCreateExercise("pushup", "chest", "bodyweight");

        // Then
        assertThat(result).isEqualTo(existing);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("운동이 없으면 새로 생성하여 반환한다")
    void findOrCreateExercise_New_CreatesExercise() {
        // Given
        when(exerciseRepository.findByName("pushup")).thenReturn(Optional.empty());
        Exercise newEx = new Exercise("pushup", "chest");
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(newEx);

        // When
        Exercise result = exerciseService.findOrCreateExercise("pushup", "chest", "bodyweight");

        // Then
        assertThat(result).isEqualTo(newEx);
        verify(exerciseRepository).save(any(Exercise.class));
    }
}
