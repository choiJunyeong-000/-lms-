package com.lms.project.LMS.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.Member;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    // ✅ 특정 교수의 시험을 조회하는 쿼리 추가
    List<Exam> findByMember(Member member);

    // ✅ 특정 강의의 시험 조회 (기존 메서드 유지)
    List<Exam> findByCourseId(Long courseId);
    // 모든 시험을 Course와 Member와 함께 조회하는 메서드 (N+1 문제 해결)
    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.member")
    List<Exam> findAllWithCourseAndMember();
    
    // 특정 강의의 시험을 Course와 Member와 함께 조회하는 메서드 (N+1 문제 해결)
    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.member WHERE e.course.id = :courseId")
    List<Exam> findByCourseIdWithMember(Long courseId);
}
