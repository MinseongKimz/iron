package com.iron.backend.service;

import com.iron.backend.domain.exercise.Exercise;
import com.iron.backend.dto.ExerciseDto;
import com.iron.backend.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    /**
     * 운동 이름으로 운동을 찾거나, 없으면 새로 생성합니다.
     * 기존 운동의 카테고리가 'OTHER'이고 새로운 카테고리 정보가 있다면 업데이트합니다.
     *
     * @param name 운동 이름
     * @param mainCategory 대분류 (가슴, 등 등)
     * @param subCategory 소분류 (바벨, 덤벨 등)
     * @return 찾거나 생성된 Exercise 객체
     */
    @Transactional
    public Exercise findOrCreateExercise(String name, String mainCategory, String subCategory) {
        Optional<Exercise> existingExercise = exerciseRepository.findByName(name);

        if (existingExercise.isPresent()) {
            Exercise exercise = existingExercise.get();
            updateCategoryIfNeeded(exercise, mainCategory, subCategory);
            return exercise;
        } else {
            return createNewExercise(name, mainCategory, subCategory);
        }
    }

    private void updateCategoryIfNeeded(Exercise exercise, String mainCategory, String subCategory) {
        // 기존 카테고리가 'OTHER'이고 새로운 분류가 유효하다면 업데이트
        if ("OTHER".equals(exercise.getMainCategory()) && mainCategory != null) {
            exercise.setMainCategory(mainCategory);
            if (subCategory != null) {
                exercise.setSubCategory(subCategory);
            }
            // Dirty checking에 의해 자동 저장되지만 명시적으로 표현 가능
        }
    }

    private Exercise createNewExercise(String name, String mainCategory, String subCategory) {
        Exercise newEx = new Exercise(name, mainCategory != null ? mainCategory : "OTHER");
        newEx.setSubCategory(subCategory);
        return exerciseRepository.save(newEx);
    }
}
