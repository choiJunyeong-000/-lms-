package com.lms.project.LMS.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Week;

import jakarta.persistence.LockModeType;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {

	// ✅ 특정 강의 ID와 주차 번호로 주차 조회
	Optional<Week> findByCourseIdAndWeekNumber(Long courseId, int weekNumber);

	// ✅ 특정 강의의 주차 목록 조회
	List<Week> findByCourseId(Long courseId);
	
	 // 주차가 이미 존재하는지 확인
    boolean existsByCourseIdAndWeekNumber(Long courseId, Integer weekNumber);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean existsByCourseAndWeekNumber(Course course, int weekNumber);
    
    Optional<Week> findFirstByWeekNumberOrderByIdAsc(int weekNumber);
	
}
