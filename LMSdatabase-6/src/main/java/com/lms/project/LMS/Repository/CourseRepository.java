package com.lms.project.LMS.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Enum.CourseStatus;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

	// 특정 멤버(교수)의 강의 조회
	List<Course> findByMemberId(Long memberId);

	// 강의 이름으로 검색
	List<Course> findByNameContaining(String name);

	// 특정 상태의 강의 조회 (CourseStatus 사용)
	List<Course> findByStatus(CourseStatus status);

	// 강의 시작일 범위로 검색
	List<Course> findByStartDateBetween(LocalDate start, LocalDate end);
	
    // 교수의 username으로 강의를 조회하는 메서드
    List<Course> findByMemberName(String professorname);

    List<Course> findByMemberStudentId(String studentId);
    
    List<Course> findByMember(Member member);
    
    
}
