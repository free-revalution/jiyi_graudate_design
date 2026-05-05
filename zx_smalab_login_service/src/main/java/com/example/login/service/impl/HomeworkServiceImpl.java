package com.example.login.service.impl;

import com.example.login.dto.response.PageResult;
import com.example.login.entity.Homework;
import com.example.login.entity.HomeworkAnswer;
import com.example.login.entity.HomeworkQuestion;

import java.math.BigDecimal;
import com.example.login.exception.BusinessException;
import com.example.login.repository.HomeworkAnswerRepository;
import com.example.login.repository.HomeworkQuestionRepository;
import com.example.login.repository.HomeworkRepository;
import com.example.login.service.HomeworkService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作业服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final HomeworkQuestionRepository homeworkQuestionRepository;
    private final HomeworkAnswerRepository homeworkAnswerRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<Homework> getHomeworkList(Long courseId, int page, int limit, String name, String status) {
        List<Homework> all = homeworkRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);

        List<Homework> filtered = all.stream()
                .filter(h -> name == null || name.isEmpty() || h.getName().contains(name))
                .filter(h -> status == null || status.isEmpty() || status.equals(h.getStatus()))
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (page - 1) * limit;
        List<Homework> list = filtered.stream()
                .skip(start)
                .limit(limit)
                .toList();

        return PageResult.<Homework>builder()
                .list(list)
                .total(total)
                .page(page)
                .limit(limit)
                .build();
    }

    @Override
    @Transactional
    public Homework createHomework(Long courseId, Homework homework, List<HomeworkQuestion> questions, Long creatorId) {
        homework.setCourseId(courseId);
        homework.setStatus("draft");
        homework.setPendingCount(0);
        homework.setSubmittedCount(0);
        homework.setUnsubmittedCount(0);
        homework.setCreatorId(creatorId);
        homework.setIsDeleted(0);
        homework.setCreatedTime(new Date());
        homework.setModifiedTime(new Date());
        Homework saved = homeworkRepository.save(homework);

        if (questions != null) {
            for (HomeworkQuestion q : questions) {
                q.setHomeworkId(saved.getId());
                q.setIsDeleted(0);
                q.setCreatedTime(new Date());
                homeworkQuestionRepository.save(q);
            }
        }
        return saved;
    }

    @Override
    public Homework getHomeworkById(Long id) {
        return homeworkRepository.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BusinessException(404, "作业不存在"));
    }

    @Override
    @Transactional
    public Homework updateHomework(Long id, Homework homework, List<HomeworkQuestion> questions) {
        Homework existing = getHomeworkById(id);
        if (homework.getName() != null) existing.setName(homework.getName());
        if (homework.getCover() != null) existing.setCover(homework.getCover());
        if (homework.getType() != null) existing.setType(homework.getType());
        if (homework.getScoreType() != null) existing.setScoreType(homework.getScoreType());
        if (homework.getStartTime() != null) existing.setStartTime(homework.getStartTime());
        if (homework.getEndTime() != null) existing.setEndTime(homework.getEndTime());
        if (homework.getClassId() != null) existing.setClassId(homework.getClassId());
        existing.setModifiedTime(new Date());
        Homework saved = homeworkRepository.save(existing);

        // If questions provided, delete old ones and create new
        if (questions != null) {
            List<HomeworkQuestion> oldQuestions = homeworkQuestionRepository
                    .findByHomeworkIdAndIsDeletedOrderBySortOrder(id, 0);
            for (HomeworkQuestion old : oldQuestions) {
                old.setIsDeleted(1);
                homeworkQuestionRepository.save(old);
            }
            for (HomeworkQuestion q : questions) {
                q.setHomeworkId(saved.getId());
                q.setIsDeleted(0);
                q.setCreatedTime(new Date());
                homeworkQuestionRepository.save(q);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteHomework(Long id) {
        Homework homework = getHomeworkById(id);
        homework.setIsDeleted(1);
        homework.setModifiedTime(new Date());
        homeworkRepository.save(homework);
    }

    @Override
    @Transactional
    public void publishHomework(Long id) {
        Homework homework = getHomeworkById(id);
        homework.setStatus("published");
        homework.setModifiedTime(new Date());
        homeworkRepository.save(homework);
    }

    @Override
    public List<Homework> getMyHomework(Long courseId, Long userId) {
        List<Homework> all = homeworkRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
        return all.stream()
                .filter(h -> "published".equals(h.getStatus()))
                .map(h -> {
                    Optional<HomeworkAnswer> answer = homeworkAnswerRepository.findByHomeworkIdAndUserId(h.getId(), userId);
                    if (answer.isPresent()) {
                        h.setSubmittedCount(h.getSubmittedCount() != null ? h.getSubmittedCount() : 0);
                    }
                    return h;
                }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getQuestionsForUser(Long homeworkId) {
        List<HomeworkQuestion> questions = homeworkQuestionRepository
                .findByHomeworkIdAndIsDeletedOrderBySortOrder(homeworkId, 0);

        List<Map<String, Object>> result = new ArrayList<>();
        String[] keys = {"A", "B", "C", "D", "E", "F", "G", "H"};

        for (HomeworkQuestion q : questions) {
            Map<String, Object> map = new LinkedHashMap<>();

            // Generate ID based on type prefix + sortOrder
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

            // Transform options: [{content:"xxx"}] -> [{key:"A", value:"xxx"}]
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
    public void submitHomework(Long homeworkId, Long userId, Map<String, Object> answers) {
        Homework homework = getHomeworkById(homeworkId);

        // Check if already submitted
        Optional<HomeworkAnswer> existing = homeworkAnswerRepository.findByHomeworkIdAndUserId(homeworkId, userId);
        if (existing.isPresent()) {
            // Update existing
            HomeworkAnswer answer = existing.get();
            try {
                answer.setAnswers(objectMapper.writeValueAsString(answers));
            } catch (Exception e) {
                throw new BusinessException("答案序列化失败");
            }
            answer.setSubmitTime(new Date());
            homeworkAnswerRepository.save(answer);
        } else {
            // Create new
            HomeworkAnswer answer = HomeworkAnswer.builder()
                    .homeworkId(homeworkId)
                    .userId(userId)
                    .answers(answers.toString())
                    .submitTime(new Date())
                    .createdTime(new Date())
                    .isDeleted(0)
                    .build();
            try {
                answer.setAnswers(objectMapper.writeValueAsString(answers));
            } catch (Exception e) {
                throw new BusinessException("答案序列化失败");
            }
            homeworkAnswerRepository.save(answer);
        }
    }

    @Override
    public List<HomeworkAnswer> getHomeworkAnswers(Long homeworkId) {
        return homeworkAnswerRepository.findByHomeworkIdAndIsDeleted(homeworkId, 0);
    }

    @Override
    public void gradeHomework(Long homeworkId, Long userId, BigDecimal score) {
        HomeworkAnswer answer = homeworkAnswerRepository.findByHomeworkIdAndUserId(homeworkId, userId)
                .orElseThrow(() -> new BusinessException(404, "未找到该学生的作业"));
        answer.setScore(score);
        homeworkAnswerRepository.save(answer);
    }
}
