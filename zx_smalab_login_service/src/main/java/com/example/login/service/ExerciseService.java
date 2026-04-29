package com.example.login.service;

import com.example.login.dto.response.PageResult;
import com.example.login.entity.ClassExercise;
import com.example.login.entity.ExerciseQuestion;

import java.util.List;
import java.util.Map;

/**
 * 课堂练习服务接口
 */
public interface ExerciseService {

    PageResult<ClassExercise> getExerciseList(Long courseId, int page, int limit, String name, String status);

    ClassExercise createExercise(Long courseId, ClassExercise exercise, List<ExerciseQuestion> questions, Long creatorId);

    ClassExercise getExerciseById(Long id);

    ClassExercise updateExercise(Long id, ClassExercise exercise, List<ExerciseQuestion> questions);

    void deleteExercise(Long id);

    void publishExercise(Long id);

    List<ClassExercise> getMyExercises(Long courseId, Long userId);

    List<Map<String, Object>> getQuestionsForUser(Long exerciseId);

    void submitExercise(Long exerciseId, Long userId, Map<String, Object> answers);
}
