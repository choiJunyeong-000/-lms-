package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 질문 데이터를 관리하는 리포지토리
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	// 특정 강의의 질문 조회
	List<Question> findByCourseId(Long courseId);

	// 특정 학생의 질문 조회
	List<Question> findByMemberId(Long memberId);

	// 특정 상태("ANSWERED", "UNANSWERED")의 질문 조회
	List<Question> findByStatus(String status);
}
