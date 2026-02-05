package com.iron.backend.controller;

import com.iron.backend.dto.EditSetsRequest;
import com.iron.backend.dto.LogWorkoutResponse;
import com.iron.backend.dto.WorkoutLogDto;
import com.iron.backend.service.WorkoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkoutControllerTest {

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutController workoutController;

    @Test
    @DisplayName("운동 로그 기록 성공 시 200 OK와 응답을 반환한다")
    void logWorkout_Success() {
        // Given
        WorkoutLogDto request = new WorkoutLogDto();
        LogWorkoutResponse responseDto = new LogWorkoutResponse();
        responseDto.setFeedback("Great!");

        given(workoutService.logWorkout(request)).willReturn(responseDto);

        // When
        ResponseEntity<LogWorkoutResponse> response = workoutController.logWorkout(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("운동 세션 삭제 성공 시 204 No Content 반환")
    void deleteWorkoutSession_Success() {
        // Given
        UUID sessionId = UUID.randomUUID();
        doNothing().when(workoutService).deleteWorkoutSession(sessionId);

        // When
        ResponseEntity<Void> response = workoutController.deleteWorkoutSession(sessionId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(workoutService).deleteWorkoutSession(sessionId);
    }

    @Test
    @DisplayName("운동 세트 수정 성공 시 204 No Content 반환")
    void editWorkoutSets_Success() {
        // Given
        EditSetsRequest request = new EditSetsRequest();
        doNothing().when(workoutService).editWorkoutSets(request);

        // When
        ResponseEntity<Void> response = workoutController.editWorkoutSets(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(workoutService).editWorkoutSets(request);
    }
}
