package com.example.login.service;

import com.example.login.dto.response.PageResult;
import com.example.login.entity.Course;
import com.example.login.entity.CourseTerm;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {

    PageResult<Course> getCourseList(int page, int limit, String name, String status);

    Course createCourse(Course course, Long creatorId);

    Course getCourseById(Long id);

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    void publishCourse(Long id);

    void closeCourse(Long id);

    List<CourseTerm> getTermsByCourseId(Long courseId);

    CourseTerm createTerm(Long courseId, CourseTerm term);

    CourseTerm updateTerm(Long courseId, Long termId, CourseTerm term);

    void deleteTerm(Long courseId, Long termId);
}
