package com.example.login.controller;

import com.example.login.dto.request.CourseRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Course;
import com.example.login.entity.CourseTerm;
import com.example.login.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ==================== Course CRUD ====================

    @GetMapping("/course/list")
    public ResponseEntity<ApiResponse<PageResult<Course>>> getCourseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        PageResult<Course> result = courseService.getCourseList(page, limit, name, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/course")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody CourseRequest request) {
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .name(request.getName())
                .englishName(request.getEnglishName())
                .coverUrl(request.getCoverUrl())
                .description(request.getDescription())
                .category(request.getCategory())
                .belongUnit(request.getBelongUnit())
                .department(request.getDepartment())
                .teacherName(request.getTeacherName())
                .totalHours(request.getTotalHours())
                .build();
        Course created = courseService.createCourse(course, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @GetMapping("/course/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(id)));
    }

    @PutMapping("/course/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Long id,
                                                            @RequestBody CourseRequest request) {
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .name(request.getName())
                .englishName(request.getEnglishName())
                .coverUrl(request.getCoverUrl())
                .description(request.getDescription())
                .category(request.getCategory())
                .belongUnit(request.getBelongUnit())
                .department(request.getDepartment())
                .teacherName(request.getTeacherName())
                .totalHours(request.getTotalHours())
                .build();
        Course updated = courseService.updateCourse(id, course);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/course/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/course/{id}/publish")
    public ResponseEntity<ApiResponse<Void>> publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
        return ResponseEntity.ok(ApiResponse.success("发布成功", null));
    }

    @PostMapping("/course/{id}/close")
    public ResponseEntity<ApiResponse<Void>> closeCourse(@PathVariable Long id) {
        courseService.closeCourse(id);
        return ResponseEntity.ok(ApiResponse.success("关闭成功", null));
    }

    // ==================== Course Terms ====================

    @GetMapping("/course/{courseId}/terms")
    public ResponseEntity<ApiResponse<java.util.List<CourseTerm>>> getTerms(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getTermsByCourseId(courseId)));
    }

    @PostMapping("/course/{courseId}/terms")
    public ResponseEntity<ApiResponse<CourseTerm>> createTerm(@PathVariable Long courseId,
                                                              @RequestBody CourseTerm term) {
        CourseTerm created = courseService.createTerm(courseId, term);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @PutMapping("/course/{courseId}/terms/{id}")
    public ResponseEntity<ApiResponse<CourseTerm>> updateTerm(@PathVariable Long courseId,
                                                              @PathVariable Long id,
                                                              @RequestBody CourseTerm term) {
        CourseTerm updated = courseService.updateTerm(courseId, id, term);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/course/{courseId}/terms/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTerm(@PathVariable Long courseId,
                                                        @PathVariable Long id) {
        courseService.deleteTerm(courseId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
