package com.example.login.controller;

import com.example.login.dto.request.NoticeRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.CourseNotice;
import com.example.login.repository.CourseNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 课程通知管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoticeController {

    private final CourseNoticeRepository courseNoticeRepository;

    @GetMapping("/course/{courseId}/notice/list")
    public ResponseEntity<ApiResponse<List<CourseNotice>>> getNoticeList(
            @PathVariable Long courseId) {
        List<CourseNotice> list = courseNoticeRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/course/{courseId}/notice")
    public ResponseEntity<ApiResponse<CourseNotice>> createNotice(
            @PathVariable Long courseId,
            @RequestBody NoticeRequest request) {
        CourseNotice notice = CourseNotice.builder()
                .courseId(courseId)
                .title(request.getTitle())
                .content(request.getContent())
                .isTop(request.getIsTop() != null ? request.getIsTop() : 0)
                .creatorId(getCurrentUserId())
                .isDeleted(0)
                .createdTime(new Date())
                .build();
        CourseNotice saved = courseNoticeRepository.save(notice);
        return ResponseEntity.ok(ApiResponse.success("创建成功", saved));
    }

    @PutMapping("/course/{courseId}/notice/{id}")
    public ResponseEntity<ApiResponse<CourseNotice>> updateNotice(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody NoticeRequest request) {
        CourseNotice notice = courseNoticeRepository.findById(id)
                .filter(n -> n.getIsDeleted() == 0)
                .orElseThrow(() -> new com.example.login.exception.BusinessException(404, "通知不存在"));

        if (request.getTitle() != null) notice.setTitle(request.getTitle());
        if (request.getContent() != null) notice.setContent(request.getContent());
        if (request.getIsTop() != null) notice.setIsTop(request.getIsTop());

        CourseNotice saved = courseNoticeRepository.save(notice);
        return ResponseEntity.ok(ApiResponse.success("更新成功", saved));
    }

    @DeleteMapping("/course/{courseId}/notice/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        CourseNotice notice = courseNoticeRepository.findById(id)
                .filter(n -> n.getIsDeleted() == 0)
                .orElseThrow(() -> new com.example.login.exception.BusinessException(404, "通知不存在"));
        notice.setIsDeleted(1);
        courseNoticeRepository.save(notice);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
