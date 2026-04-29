package com.example.login.controller;

import com.example.login.dto.request.ClassRequest;
import com.example.login.dto.request.CourseRequest;
import com.example.login.dto.request.StudentRequest;
import com.example.login.dto.request.TeacherRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Course;
import com.example.login.entity.CourseClass;
import com.example.login.entity.CourseStudent;
import com.example.login.entity.CourseTeacher;
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

    // ==================== Teacher Team ====================

    @GetMapping("/course/{courseId}/teachers")
    public ResponseEntity<ApiResponse<java.util.List<CourseTeacher>>> getTeachers(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getTeachersByCourseId(courseId)));
    }

    @PostMapping("/course/{courseId}/teachers")
    public ResponseEntity<ApiResponse<CourseTeacher>> createTeacher(
            @PathVariable Long courseId,
            @RequestBody TeacherRequest request) {
        CourseTeacher teacher = CourseTeacher.builder()
                .name(request.getName())
                .role(request.getRole())
                .workNo(request.getWorkNo())
                .department(request.getDepartment())
                .build();
        CourseTeacher created = courseService.createTeacher(courseId, teacher);
        return ResponseEntity.ok(ApiResponse.success("添加成功", created));
    }

    @PutMapping("/course/{courseId}/teachers/{id}")
    public ResponseEntity<ApiResponse<CourseTeacher>> updateTeacher(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody TeacherRequest request) {
        CourseTeacher teacher = CourseTeacher.builder()
                .name(request.getName())
                .role(request.getRole())
                .workNo(request.getWorkNo())
                .department(request.getDepartment())
                .build();
        CourseTeacher updated = courseService.updateTeacher(courseId, id, teacher);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/course/{courseId}/teachers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        courseService.deleteTeacher(courseId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    // ==================== Class Management ====================

    @GetMapping("/course/{courseId}/classes")
    public ResponseEntity<ApiResponse<java.util.List<CourseClass>>> getClasses(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getClassesByCourseId(courseId)));
    }

    @PostMapping("/course/{courseId}/classes")
    public ResponseEntity<ApiResponse<CourseClass>> createClass(
            @PathVariable Long courseId,
            @RequestBody ClassRequest request) {
        CourseClass courseClass = CourseClass.builder()
                .name(request.getName())
                .build();
        CourseClass created = courseService.createClass(courseId, courseClass);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    // ==================== Student Management ====================

    @GetMapping("/course/{courseId}/class/{classId}/students")
    public ResponseEntity<ApiResponse<java.util.List<CourseStudent>>> getStudents(
            @PathVariable Long courseId,
            @PathVariable Long classId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getStudentsByClassId(courseId, classId)));
    }

    @PostMapping("/course/{courseId}/class/{classId}/students")
    public ResponseEntity<ApiResponse<CourseStudent>> createStudent(
            @PathVariable Long courseId,
            @PathVariable Long classId,
            @RequestBody StudentRequest request) {
        CourseStudent student = CourseStudent.builder()
                .userId(request.getUserId())
                .studentId(request.getStudentId())
                .name(request.getName())
                .department(request.getDepartment())
                .major(request.getMajor())
                .className(request.getClassName())
                .build();
        CourseStudent created = courseService.createStudent(courseId, classId, student);
        return ResponseEntity.ok(ApiResponse.success("添加成功", created));
    }

    @DeleteMapping("/course/{courseId}/class/{classId}/students/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long courseId,
            @PathVariable Long classId,
            @PathVariable Long id) {
        courseService.deleteStudent(courseId, classId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
