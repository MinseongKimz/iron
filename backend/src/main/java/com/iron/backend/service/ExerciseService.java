package com.iron.backend.service;

import com.iron.backend.domain.exercise.Exercise;
import com.iron.backend.dto.ExerciseDto;
import com.iron.backend.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public List<ExerciseDto> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ExerciseDto mapToDto(Exercise exercise) {
        return ExerciseDto.builder()
                .exerciseId(exercise.getExerciseId())
                .name(exercise.getName())
                .mainCategory(exercise.getMainCategory())
                .subCategory(exercise.getSubCategory())
                .synonyms(exercise.getSynonyms())
                .build();
    }
}
