package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 성적 데이터를 관리하는 리포지토리
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

	// ✅ 기존 코드 유지 & 에러 해결
	// 특정 학생의 모든 성적 조회
	List<Grade> findByStudent_Id(Long studentId); // 🔥 JPA가 인식할 수 있도록 수정

	// ✅ 기존 코드 유지
	// 특정 강의의 모든 성적 조회
	List<Grade> findByCourseId(Long courseId);

	// ✅ 기존 코드 유지 & 에러 해결
	// 특정 학생이 특정 강의에서 받은 성적 조회
	List<Grade> findByStudent_IdAndCourseId(Long studentId, Long courseId); // 🔥 JPA가 인식할 수 있도록 수정

	// ✅ 기존 코드 유지
	// 특정 강의에서 특정 상태("PASSED", "FAILED")의 성적 조회
	List<Grade> findByCourseIdAndStatus(Long courseId, String status);

	// 🔹 교수의 성적 조회 기능 추가 (필요하면 사용)
	List<Grade> findByProfessor_Id(Long professorId); // 🔥 교수별 성적 조회 기능 추가
}
