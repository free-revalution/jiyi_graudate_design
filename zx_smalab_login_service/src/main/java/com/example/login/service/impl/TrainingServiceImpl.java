package com.example.login.service.impl;

import com.example.login.dto.request.TrainingNodeRequest;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Training;
import com.example.login.entity.TrainingNode;
import com.example.login.entity.TrainingStageScore;
import com.example.login.entity.TrainingStudentTask;
import com.example.login.exception.BusinessException;
import com.example.login.repository.TrainingNodeRepository;
import com.example.login.repository.TrainingRepository;
import com.example.login.repository.TrainingStageScoreRepository;
import com.example.login.repository.TrainingStudentTaskRepository;
import com.example.login.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实训服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingNodeRepository trainingNodeRepository;
    private final TrainingStudentTaskRepository trainingStudentTaskRepository;
    private final TrainingStageScoreRepository trainingStageScoreRepository;

    @Override
    public PageResult<Training> getTrainingList(Long courseId, int page, int limit, String name, String status) {
        List<Training> all = trainingRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);

        List<Training> filtered = all.stream()
                .filter(t -> name == null || name.isEmpty() || t.getName().contains(name))
                .filter(t -> status == null || status.isEmpty() || status.equals(t.getStatus()))
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (page - 1) * limit;
        List<Training> list = filtered.stream()
                .skip(start)
                .limit(limit)
                .collect(Collectors.toList());

        return PageResult.<Training>builder()
                .list(list)
                .total(total)
                .page(page)
                .limit(limit)
                .build();
    }

    @Override
    @Transactional
    public Training createTraining(Long courseId, Training training, Long creatorId) {
        training.setCourseId(courseId);
        training.setStatus("未发布");
        training.setPendingCount(0);
        training.setSubmittedCount(0);
        training.setUnsubmittedCount(0);
        training.setCreatorId(creatorId);
        training.setIsDeleted(0);
        training.setCreatedTime(new Date());
        training.setModifiedTime(new Date());
        return trainingRepository.save(training);
    }

    @Override
    public Training getTrainingById(Long id) {
        return trainingRepository.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BusinessException(404, "实训不存在"));
    }

    @Override
    @Transactional
    public Training updateTraining(Long id, Training training) {
        Training existing = getTrainingById(id);
        if (training.getName() != null) existing.setName(training.getName());
        if (training.getCover() != null) existing.setCover(training.getCover());
        if (training.getDescription() != null) existing.setDescription(training.getDescription());
        if (training.getStartTime() != null) existing.setStartTime(training.getStartTime());
        if (training.getEndTime() != null) existing.setEndTime(training.getEndTime());
        existing.setModifiedTime(new Date());
        return trainingRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTraining(Long id) {
        Training training = getTrainingById(id);
        training.setIsDeleted(1);
        training.setModifiedTime(new Date());
        trainingRepository.save(training);
    }

    @Override
    @Transactional
    public void publishTraining(Long id) {
        Training training = getTrainingById(id);
        training.setStatus("进行中");
        training.setModifiedTime(new Date());
        trainingRepository.save(training);
    }

    @Override
    public List<Map<String, Object>> getTrainingNodes(Long trainingId) {
        List<TrainingNode> allNodes = trainingNodeRepository.findByTrainingIdAndIsDeletedOrderBySortOrder(trainingId, 0);

        // Build tree from flat list using parentId
        Map<Long, List<TrainingNode>> childrenMap = allNodes.stream()
                .collect(Collectors.groupingBy(n -> n.getParentId() != null ? n.getParentId() : 0L));

        return buildTree(childrenMap, 0L);
    }

    private List<Map<String, Object>> buildTree(Map<Long, List<TrainingNode>> childrenMap, Long parentId) {
        List<TrainingNode> children = childrenMap.getOrDefault(parentId, Collections.emptyList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (TrainingNode node : children) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", node.getId());
            map.put("index", node.getNodeIndex());
            map.put("label", node.getLabel());
            map.put("content", node.getContent());
            map.put("children", buildTree(childrenMap, node.getId()));
            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional
    public void saveTrainingNodes(Long trainingId, List<TrainingNodeRequest> nodes) {
        // 1. Delete all existing nodes for this training
        trainingNodeRepository.deleteByTrainingId(trainingId);
        // 2. Recursively traverse the tree and insert nodes
        saveNodesRecursively(trainingId, nodes, 0L);
    }

    private void saveNodesRecursively(Long trainingId, List<TrainingNodeRequest> nodes, Long parentId) {
        for (int i = 0; i < nodes.size(); i++) {
            TrainingNodeRequest node = nodes.get(i);
            TrainingNode entity = TrainingNode.builder()
                    .trainingId(trainingId)
                    .parentId(parentId)
                    .nodeIndex(node.getNodeIndex())
                    .label(node.getLabel())
                    .content(node.getContent())
                    .sortOrder(i)
                    .isDeleted(0)
                    .createdTime(new Date())
                    .build();
            trainingNodeRepository.save(entity);
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                saveNodesRecursively(trainingId, node.getChildren(), entity.getId());
            }
        }
    }

    @Override
    public List<TrainingStudentTask> getTrainingStudents(Long trainingId) {
        return trainingStudentTaskRepository.findByTrainingIdAndIsDeleted(trainingId, 0);
    }

    @Override
    public Map<String, Object> getTrainingStudentDetail(Long trainingId, Long userId) {
        TrainingStudentTask task = trainingStudentTaskRepository
                .findByTrainingIdAndUserIdAndIsDeleted(trainingId, userId, 0)
                .orElseThrow(() -> new BusinessException(404, "学生任务不存在"));

        List<TrainingStageScore> stageScores = trainingStageScoreRepository.findByTaskId(task.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("student", task);
        result.put("stageScores", stageScores);
        return result;
    }

    @Override
    public List<Training> getMyTrainings(Long courseId, Long userId) {
        List<Training> all = trainingRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
        return all.stream()
                .filter(t -> trainingStudentTaskRepository
                        .findByTrainingIdAndUserIdAndIsDeleted(t.getId(), userId, 0).isPresent())
                .toList();
    }

    @Override
    public Map<String, Object> getTrainingDetail(Long trainingId) {
        Training training = getTrainingById(trainingId);
        List<Map<String, Object>> nodes = getTrainingNodes(trainingId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("training", training);
        result.put("nodes", nodes);
        return result;
    }
}
