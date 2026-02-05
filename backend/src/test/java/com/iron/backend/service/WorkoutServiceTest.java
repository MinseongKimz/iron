package com.iron.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iron.backend.domain.exercise.Exercise;
import com.iron.backend.domain.user.User;
import com.iron.backend.domain.workout.WorkoutSession;
import com.iron.backend.domain.workout.WorkoutSet;
import com.iron.backend.dto.AiWorkoutResult;
import com.iron.backend.dto.LogWorkoutResponse;
import com.iron.backend.dto.WorkoutLogDto;
import com.iron.backend.repository.UserRepository;
import com.iron.backend.repository.WorkoutSessionRepository;
import com.iron.backend.repository.WorkoutSetRepository;
import com.iron.backend.service.ai.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private GeminiService geminiService;
    @Mock
    private AnalysisService analysisService;
    @Mock
    private ExerciseService exerciseService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WorkoutSessionRepository workoutSessionRepository;
    @Mock
    private WorkoutSetRepository workoutSetRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WorkoutService workoutService;

    private User user;
    private WorkoutLogDto logDto;

    @BeforeEach
    void setUp() {
        user = new User("test@test.com", "pw", "user");
        user.setUserId(1L);
        user.setGeminiApiKey("valid-key");

        logDto = new WorkoutLogDto();
        logDto.setUserId(1L);
        logDto.setRawInput("벤치프레스 60kg 10회");
    }

    @Test
    @DisplayName("정상적으로 운동 로그를 생성하고 저장한다")
    void logWorkout_Success() throws Exception {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        AiWorkoutResult mockResult = new AiWorkoutResult();
        mockResult.setFeedback("Good!");
        AiWorkoutResult.AiExercise aiEx = new AiWorkoutResult.AiExercise();
        aiEx.setName("벤치프레스");
        aiEx.setMain_category("가슴");
        
        AiWorkoutResult.AiSet aiSet = new AiWorkoutResult.AiSet();
        aiSet.setWeight(60.0);
        aiSet.setReps(10);
        
        aiEx.setSets(List.of(aiSet));
        mockResult.setExercises(List.of(aiEx));

        when(geminiService.parseWorkoutLog(anyString(), anyString())).thenReturn(mockResult);
        
        Exercise mockExercise = new Exercise("벤치프레스", "가슴");
        when(exerciseService.findOrCreateExercise(eq("벤치프레스"), eq("가슴"), any())).thenReturn(mockExercise);
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"mock\"}");

        // When
        LogWorkoutResponse response = workoutService.logWorkout(logDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getFeedback()).isEqualTo("Good!");
        
        verify(workoutSessionRepository).save(any(WorkoutSession.class));
        verify(workoutSetRepository, times(1)).save(any(WorkoutSet.class));
        verify(exerciseService).findOrCreateExercise(eq("벤치프레스"), eq("가슴"), any());
    }
    
    @Test
    @DisplayName("API 키가 없으면 예외를 던진다")
    void logWorkout_NoApiKey_ThrowsException() {
        user.setGeminiApiKey(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        assertThatThrownBy(() -> workoutService.logWorkout(logDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("API Key is missing");
    }
}
