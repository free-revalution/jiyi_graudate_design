package com.example.login.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 实训节点请求DTO（树形结构）
 */
@Data
public class TrainingNodeRequest {

    private String nodeIndex;
    private String label;
    private String content;
    private List<TrainingNodeRequest> children;
}
