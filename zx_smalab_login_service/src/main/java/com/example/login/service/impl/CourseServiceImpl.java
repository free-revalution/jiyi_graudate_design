package com.example.login.service.impl;

import com.example.login.dto.response.PageResult;
import com.example.login.entity.Course;
import com.example.login.entity.CourseClass;
import com.example.login.entity.CourseStudent;
import com.example.login.entity.CourseTeacher;
import com.example.login.entity.CourseTerm;
import com.example.login.exception.BusinessException;
import com.example.login.repository.*;
import com.example.login.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseTermRepository courseTermRepository;
    private final CourseTeacherRepository courseTeacherRepository;
    private final CourseClassRepository courseClassRepository;
    private final CourseStudentRepository courseStudentRepository;

    @Override
    public PageResult<Course> getCourseList(int page, int limit, String name, String status) {
        List<Course> all = courseRepository.findByIsDeleted(0);

        List<Course> filtered = all.stream()
                .filter(c -> name == null || name.isEmpty() || c.getName().contains(name))
                .filter(c -> status == null || status.isEmpty() || status.equals(c.getStatus()))
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (page - 1) * limit;
        List<Course> list = filtered.stream()
                .skip(start)
                .limit(limit)
                .toList();

        return PageResult.<Course>builder()
                .list(list)
                .total(total)
                .page(page)
                .limit(limit)
                .build();
    }

    @Override
    @Transactional
    public Course createCourse(Course course, Long creatorId) {
        course.setCreatorId(creatorId);
        course.setStatus("draft");
        course.setIsDeleted(0);
        course.setCreatedTime(new Date());
        course.setModifiedTime(new Date());
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BusinessException(404, "课程不存在"));
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, Course course) {
        Course existing = getCourseById(id);
        if (course.getName() != null) existing.setName(course.getName());
        if (course.getEnglishName() != null) existing.setEnglishName(course.getEnglishName());
        if (course.getCoverUrl() != null) existing.setCoverUrl(course.getCoverUrl());
        if (course.getDescription() != null) existing.setDescription(course.getDescription());
        if (course.getCategory() != null) existing.setCategory(course.getCategory());
        if (course.getBelongUnit() != null) existing.setBelongUnit(course.getBelongUnit());
        if (course.getDepartment() != null) existing.setDepartment(course.getDepartment());
        if (course.getTeacherName() != null) existing.setTeacherName(course.getTeacherName());
        if (course.getTotalHours() != null) existing.setTotalHours(course.getTotalHours());
        if (course.getCourseCode() != null) existing.setCourseCode(course.getCourseCode());
        existing.setModifiedTime(new Date());
        return courseRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        course.setIsDeleted(1);
        course.setModifiedTime(new Date());
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void publishCourse(Long id) {
        Course course = getCourseById(id);
        course.setStatus("published");
        course.setModifiedTime(new Date());
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void closeCourse(Long id) {
        Course course = getCourseById(id);
        course.setStatus("closed");
        course.setModifiedTime(new Date());
        courseRepository.save(course);
    }

    @Override
    public List<CourseTerm> getTermsByCourseId(Long courseId) {
        return courseTermRepository.findByCourseIdAndIsDeleted(courseId, 0);
    }

    @Override
    @Transactional
    public CourseTerm createTerm(Long courseId, CourseTerm term) {
        term.setCourseId(courseId);
        term.setIsDeleted(0);
        term.setCreatedTime(new Date());
        return courseTermRepository.save(term);
    }

    @Override
    @Transactional
    public CourseTerm updateTerm(Long courseId, Long termId, CourseTerm term) {
        CourseTerm existing = courseTermRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(404, "学期不存在"));
        if (!existing.getCourseId().equals(courseId)) {
            throw new BusinessException(400, "学期不属于该课程");
        }
        if (term.getName() != null) existing.setName(term.getName());
        if (term.getStartTime() != null) existing.setStartTime(term.getStartTime());
        if (term.getEndTime() != null) existing.setEndTime(term.getEndTime());
        if (term.getStatus() != null) existing.setStatus(term.getStatus());
        if (term.getInfo() != null) existing.setInfo(term.getInfo());
        if (term.getContent() != null) existing.setContent(term.getContent());
        return courseTermRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTerm(Long courseId, Long termId) {
        CourseTerm term = courseTermRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(404, "学期不存在"));
        if (!term.getCourseId().equals(courseId)) {
            throw new BusinessException(400, "学期不属于该课程");
        }
        term.setIsDeleted(1);
        courseTermRepository.save(term);
    }

    // ==================== Teacher Team Management ====================

    @Override
    public List<CourseTeacher> getTeachersByCourseId(Long courseId) {
        return courseTeacherRepository.findByCourseIdAndIsDeleted(courseId, 0);
    }

    @Override
    @Transactional
    public CourseTeacher createTeacher(Long courseId, CourseTeacher teacher) {
        teacher.setCourseId(courseId);
        teacher.setIsDeleted(0);
        teacher.setJoinTime(new Date());
        teacher.setCreatedTime(new Date());
        return courseTeacherRepository.save(teacher);
    }

    @Override
    @Transactional
    public CourseTeacher updateTeacher(Long courseId, Long teacherId, CourseTeacher teacher) {
        CourseTeacher existing = courseTeacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException(404, "教师不存在"));
        if (!existing.getCourseId().equals(courseId)) {
            throw new BusinessException(400, "教师不属于该课程");
        }
        if (teacher.getName() != null) existing.setName(teacher.getName());
        if (teacher.getRole() != null) existing.setRole(teacher.getRole());
        if (teacher.getWorkNo() != null) existing.setWorkNo(teacher.getWorkNo());
        if (teacher.getDepartment() != null) existing.setDepartment(teacher.getDepartment());
        return courseTeacherRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTeacher(Long courseId, Long teacherId) {
        CourseTeacher teacher = courseTeacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException(404, "教师不存在"));
        if (!teacher.getCourseId().equals(courseId)) {
            throw new BusinessException(400, "教师不属于该课程");
        }
        teacher.setIsDeleted(1);
        courseTeacherRepository.save(teacher);
    }

    // ==================== Class Management ====================

    @Override
    public List<CourseClass> getClassesByCourseId(Long courseId) {
        return courseClassRepository.findByCourseIdAndIsDeleted(courseId, 0);
    }

    @Override
    @Transactional
    public CourseClass createClass(Long courseId, CourseClass courseClass) {
        courseClass.setCourseId(courseId);
        courseClass.setStudentCount(0);
        courseClass.setIsDeleted(0);
        courseClass.setCreatedTime(new Date());
        return courseClassRepository.save(courseClass);
    }

    // ==================== Student Management ====================

    @Override
    public List<CourseStudent> getStudentsByClassId(Long courseId, Long classId) {
        return courseStudentRepository.findByClassIdAndIsDeleted(classId, 0);
    }

    @Override
    @Transactional
    public CourseStudent createStudent(Long courseId, Long classId, CourseStudent student) {
        student.setCourseId(courseId);
        student.setClassId(classId);
        student.setJoinTime(new Date());
        student.setCreatedTime(new Date());
        student.setIsDeleted(0);
        CourseStudent saved = courseStudentRepository.save(student);
        // Update student count
        courseClassRepository.findById(classId).ifPresent(cls -> {
            cls.setStudentCount((int) courseStudentRepository.countByClassIdAndIsDeleted(classId, 0));
            courseClassRepository.save(cls);
        });
        return saved;
    }

    @Override
    @Transactional
    public void deleteStudent(Long courseId, Long classId, Long studentId) {
        CourseStudent student = courseStudentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(404, "学生不存在"));
        if (!student.getClassId().equals(classId) || !student.getCourseId().equals(courseId)) {
            throw new BusinessException(400, "学生不属于该课程班级");
        }
        student.setIsDeleted(1);
        courseStudentRepository.save(student);
        // Update student count
        courseClassRepository.findById(classId).ifPresent(cls -> {
            cls.setStudentCount((int) courseStudentRepository.countByClassIdAndIsDeleted(classId, 0));
            courseClassRepository.save(cls);
        });
    }
}
