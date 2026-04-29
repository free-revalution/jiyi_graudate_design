package com.example.login.service;

import com.example.login.dto.response.PageResult;
import com.example.login.entity.Homework;
import com.example.login.entity.HomeworkQuestion;

import java.util.List;
import java.util.Map;

/**
 * 作业服务接口
 */
public interface HomeworkService {

    PageResult<Homework> getHomeworkList(Long courseId, int page, int limit, String name, String status);

    Homework createHomework(Long courseId, Homework homework, List<HomeworkQuestion> questions, Long creatorId);

    Homework getHomeworkById(Long id);

    Homework updateHomework(Long id, Homework homework, List<HomeworkQuestion> questions);

    void deleteHomework(Long id);

    void publishHomework(Long id);

    List<Homework> getMyHomework(Long courseId, Long userId);

    List<Map<String, Object>> getQuestionsForUser(Long homeworkId);

    void submitHomework(Long homeworkId, Long userId, Map<String, Object> answers);
}
