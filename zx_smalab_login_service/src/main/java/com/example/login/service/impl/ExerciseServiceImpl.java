package com.example.login.service.impl;

import com.example.login.dto.response.PageResult;
import com.example.login.entity.ClassExercise;
import com.example.login.entity.ExerciseAnswer;
import com.example.login.entity.ExerciseQuestion;
import com.example.login.exception.BusinessException;
import com.example.login.repository.ClassExerciseRepository;
import com.example.login.repository.ExerciseAnswerRepository;
import com.example.login.repository.ExerciseQuestionRepository;
import com.example.login.service.ExerciseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 课堂练习服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ClassExerciseRepository classExerciseRepository;
    private final ExerciseQuestionRepository exerciseQuestionRepository;
    private final ExerciseAnswerRepository exerciseAnswerRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<ClassExercise> getExerciseList(Long courseId, int page, int limit, String name, String status) {
        List<ClassExercise> all = classExerciseRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);

        List<ClassExercise> filtered = all.stream()
                .filter(e -> name == null || name.isEmpty() || e.getName().contains(name))
                .filter(e -> status == null || status.isEmpty() || status.equals(e.getStatus()))
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (page - 1) * limit;
        List<ClassExercise> list = filtered.stream()
                .skip(start)
                .limit(limit)
                .toList();

        return PageResult.<ClassExercise>builder()
                .list(list)
                .total(total)
                .page(page)
                .limit(limit)
                .build();
    }

    @Override
    @Transactional
    public ClassExercise createExercise(Long courseId, ClassExercise exercise, List<ExerciseQuestion> questions, Long creatorId) {
        exercise.setCourseId(courseId);
        exercise.setStatus("draft");
        exercise.setCompletedCount(0);
        exercise.setUncompletedCount(0);
        exercise.setQuestionCount(questions != null ? questions.size() : 0);
        exercise.setCreatorId(creatorId);
        exercise.setIsDeleted(0);
        exercise.setCreatedTime(new Date());
        exercise.setModifiedTime(new Date());
        ClassExercise saved = classExerciseRepository.save(exercise);

        if (questions != null) {
            for (ExerciseQuestion q : questions) {
                q.setExerciseId(saved.getId());
                q.setIsDeleted(0);
                q.setCreatedTime(new Date());
                exerciseQuestionRepository.save(q);
            }
        }
        return saved;
    }

    @Override
    public ClassExercise getExerciseById(Long id) {
        return classExerciseRepository.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BusinessException(404, "课堂练习不存在"));
    }

    @Override
    @Transactional
    public ClassExercise updateExercise(Long id, ClassExercise exercise, List<ExerciseQuestion> questions) {
        ClassExercise existing = getExerciseById(id);
        if (exercise.getName() != null) existing.setName(exercise.getName());
        if (exercise.getType() != null) existing.setType(exercise.getType());
        if (exercise.getScoreType() != null) existing.setScoreType(exercise.getScoreType());
        if (exercise.getStartTime() != null) existing.setStartTime(exercise.getStartTime());
        if (exercise.getEndTime() != null) existing.setEndTime(exercise.getEndTime());
        existing.setModifiedTime(new Date());
        ClassExercise saved = classExerciseRepository.save(existing);

        if (questions != null) {
            List<ExerciseQuestion> oldQuestions = exerciseQuestionRepository
                    .findByExerciseIdAndIsDeletedOrderBySortOrder(id, 0);
            for (ExerciseQuestion old : oldQuestions) {
                old.setIsDeleted(1);
                exerciseQuestionRepository.save(old);
            }
            for (ExerciseQuestion q : questions) {
                q.setExerciseId(saved.getId());
                q.setIsDeleted(0);
                q.setCreatedTime(new Date());
                exerciseQuestionRepository.save(q);
            }
            saved.setQuestionCount(questions.size());
            classExerciseRepository.save(saved);
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        ClassExercise exercise = getExerciseById(id);
        exercise.setIsDeleted(1);
        exercise.setModifiedTime(new Date());
        classExerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public void publishExercise(Long id) {
        ClassExercise exercise = getExerciseById(id);
        exercise.setStatus("published");
        exercise.setModifiedTime(new Date());
        classExerciseRepository.save(exercise);
    }

    @Override
    public List<ClassExercise> getMyExercises(Long courseId, Long userId) {
        return classExerciseRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
    }

    @Override
    public List<Map<String, Object>> getQuestionsForUser(Long exerciseId) {
        List<ExerciseQuestion> questions = exerciseQuestionRepository
                .findByExerciseIdAndIsDeletedOrderBySortOrder(exerciseId, 0);

        List<Map<String, Object>> result = new ArrayList<>();
        String[] keys = {"A", "B", "C", "D", "E", "F", "G", "H"};

        for (ExerciseQuestion q : questions) {
            Map<String, Object> map = new LinkedHashMap<>();

            String prefix;
            switch (q.getType()) {
                case "single": prefix = "s"; break;
                case "multiple": prefix = "m"; break;
                case "judge": prefix = "j"; break;
                case "fill": prefix = "f"; break;
                default: prefix = "q"; break;
            }
            map.put("id", prefix + q.getSortOrder());
            map.put("type", q.getType());
            map.put("title", q.getContent());

            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                try {
                    List<Map<String, String>> options = objectMapper.readValue(q.getOptions(),
                            new TypeReference<List<Map<String, String>>>() {});
                    List<Map<String, String>> transformed = new ArrayList<>();
                    for (int i = 0; i < options.size(); i++) {
                        Map<String, String> opt = new LinkedHashMap<>();
                        opt.put("key", keys[i]);
                        opt.put("value", options.get(i).getOrDefault("content", ""));
                        transformed.add(opt);
                    }
                    map.put("options", transformed);
                } catch (Exception e) {
                    log.error("Failed to parse options for question {}", q.getId(), e);
                }
            }

            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional
    public void submitExercise(Long exerciseId, Long userId, Map<String, Object> answers) {
        getExerciseById(exerciseId);

        Optional<ExerciseAnswer> existing = exerciseAnswerRepository.findByExerciseIdAndUserId(exerciseId, userId);
        if (existing.isPresent()) {
            ExerciseAnswer answer = existing.get();
            try {
                answer.setAnswers(objectMapper.writeValueAsString(answers));
            } catch (Exception e) {
                throw new BusinessException("答案序列化失败");
            }
            answer.setSubmitTime(new Date());
            exerciseAnswerRepository.save(answer);
        } else {
            ExerciseAnswer answer = ExerciseAnswer.builder()
                    .exerciseId(exerciseId)
                    .userId(userId)
                    .createdTime(new Date())
                    .build();
            try {
                answer.setAnswers(objectMapper.writeValueAsString(answers));
            } catch (Exception e) {
                throw new BusinessException("答案序列化失败");
            }
            answer.setSubmitTime(new Date());
            exerciseAnswerRepository.save(answer);
        }
    }
}
