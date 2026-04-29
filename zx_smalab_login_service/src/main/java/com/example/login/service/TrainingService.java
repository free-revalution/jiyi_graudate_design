package com.example.login.service;

import com.example.login.dto.request.TrainingNodeRequest;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Training;
import com.example.login.entity.TrainingNode;
import com.example.login.entity.TrainingStudentTask;

import java.util.List;
import java.util.Map;

/**
 * 实训服务接口
 */
public interface TrainingService {

    PageResult<Training> getTrainingList(Long courseId, int page, int limit, String name);

    Training createTraining(Long courseId, Training training, Long creatorId);

    Training getTrainingById(Long id);

    Training updateTraining(Long id, Training training);

    void deleteTraining(Long id);

    void publishTraining(Long id);

    List<Map<String, Object>> getTrainingNodes(Long trainingId);

    void saveTrainingNodes(Long trainingId, List<TrainingNodeRequest> nodes);

    List<TrainingStudentTask> getTrainingStudents(Long trainingId);

    Map<String, Object> getTrainingStudentDetail(Long trainingId, Long userId);

    List<Training> getMyTrainings(Long courseId, Long userId);

    Map<String, Object> getTrainingDetail(Long trainingId);
}
