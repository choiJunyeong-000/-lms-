package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Assignment;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 과제 데이터를 관리하는 리포지토리
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	// 특정 강의의 과제 조회
	List<Assignment> findByCourseId(Long courseId);

	// 마감일 이전의 과제 조회
	List<Assignment> findByDueDateBefore(LocalDateTime dueDate);

	// 활성화 상태의 과제 조회
	List<Assignment> findByIsActiveTrue();
	
	 List<Assignment> findByCourseIdAndWeek_WeekNumber(Long courseId, int weekNumber);
	 
	 List<Assignment> findAllByWeekId(Long weekId);
	 

}
