package com.lms.project.LMS.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.CourseQnA;

public interface CourseQnARepository extends JpaRepository<CourseQnA, Long> {
    Page<CourseQnA> findByCourseId(Long courseId, Pageable pageable);
    List<CourseQnA> findByCourse(Course course);
    void deleteAllByCourse(Course course);
    void deleteByCourseId(Long courseId);
}