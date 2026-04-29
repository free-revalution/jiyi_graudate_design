package com.example.login.controller;

import com.example.login.dto.request.ScoreWeightRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.ScoreWeight;
import com.example.login.exception.BusinessException;
import com.example.login.repository.ScoreWeightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * 成绩权重管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScoreWeightController {

    private final ScoreWeightRepository scoreWeightRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/course/{courseId}/score-weight")
    public ResponseEntity<ApiResponse<Object>> getScoreWeight(
            @PathVariable Long courseId) {
        Optional<ScoreWeight> opt = scoreWeightRepository.findByCourseId(courseId);
        if (opt.isPresent()) {
            try {
                Object weights = objectMapper.readValue(opt.get().getWeights(), Object.class);
                return ResponseEntity.ok(ApiResponse.success(weights));
            } catch (Exception e) {
                return ResponseEntity.ok(ApiResponse.success(opt.get().getWeights()));
            }
        }
        return ResponseEntity.ok(ApiResponse.success(Map.of()));
    }

    @PutMapping("/course/{courseId}/score-weight")
    public ResponseEntity<ApiResponse<Void>> saveScoreWeight(
            @PathVariable Long courseId,
            @RequestBody ScoreWeightRequest request) {
        try {
            String weightsJson = objectMapper.writeValueAsString(request.getWeights());

            Optional<ScoreWeight> opt = scoreWeightRepository.findByCourseId(courseId);
            if (opt.isPresent()) {
                ScoreWeight existing = opt.get();
                existing.setWeights(weightsJson);
                existing.setUpdatedTime(new Date());
                scoreWeightRepository.save(existing);
            } else {
                ScoreWeight weight = ScoreWeight.builder()
                        .courseId(courseId)
                        .weights(weightsJson)
                        .updatedTime(new Date())
                        .build();
                scoreWeightRepository.save(weight);
            }
            return ResponseEntity.ok(ApiResponse.success("保存成功", null));
        } catch (Exception e) {
            throw new BusinessException("权重数据序列化失败");
        }
    }
}
