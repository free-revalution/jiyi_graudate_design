package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Course;
import com.example.login.entity.CourseTeacher;
import com.example.login.entity.CourseTerm;
import com.example.login.repository.CourseRepository;
import com.example.login.repository.CourseStudentRepository;
import com.example.login.repository.CourseTeacherRepository;
import com.example.login.repository.CourseTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页公开API控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseTeacherRepository courseTeacherRepository;
    private final CourseTermRepository courseTermRepository;

    @GetMapping("/home/hot-courses")
    public ResponseEntity<ApiResponse<List<Course>>> getHotCourses() {
        List<Course> courses = courseRepository.findTop4ByStatusAndIsDeletedOrderByCreatedTimeDesc("published", 0);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/home/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("courseCount", courseRepository.countByIsDeleted(0));
        stats.put("studentCount", courseStudentRepository.countByIsDeleted(0));
        stats.put("teacherCount", courseTeacherRepository.countByIsDeleted(0));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResult<Course>>> getPublicCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        List<Course> all = courseRepository.findByStatusAndIsDeleted("published", 0);

        List<Course> filtered = all.stream()
                .filter(c -> keyword == null || keyword.isEmpty()
                        || c.getName().contains(keyword)
                        || (c.getDescription() != null && c.getDescription().contains(keyword)))
                .filter(c -> category == null || category.isEmpty() || category.equals(c.getCategory()))
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (page - 1) * limit;
        List<Course> list = filtered.stream()
                .skip(start)
                .limit(limit)
                .toList();

        PageResult<Course> result = PageResult.<Course>builder()
                .list(list)
                .total(total)
                .page(page)
                .limit(limit)
                .build();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicCourse(@PathVariable Long id) {
        Course course = courseRepository.findByIdAndIsDeleted(id, 0)
                .orElse(null);
        if (course == null || !"published".equals(course.getStatus())) {
            return ResponseEntity.ok(ApiResponse.error(404, "课程不存在"));
        }

        List<CourseTeacher> teachers = courseTeacherRepository.findByCourseIdAndIsDeleted(id, 0);
        List<CourseTerm> terms = courseTermRepository.findByCourseIdAndIsDeleted(id, 0);

        Map<String, Object> data = new HashMap<>();
        data.put("course", course);
        data.put("teachers", teachers);
        data.put("terms", terms);

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
