package com.lms.project.LMS.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.EssayExamQuestion;

/**
 * 서술형 시험 문제(EssayExamQuestion) 데이터를 관리하는 리포지토리 - 기본적인 CRUD 기능 제공 (JpaRepository
 * 자동 포함) - 특정 시험 문제 ID를 기반으로 서술형 문제 조회 기능 포함
 */
public interface EssayExamQuestionRepository extends JpaRepository<EssayExamQuestion, Long> {

	/**
	 * 특정 시험 문제(ExamQuestion)의 ID로 서술형 문제 조회
	 * 
	 * @param questionId ExamQuestion의 ID
	 * @return 해당 ID를 가진 서술형 문제 (없으면 Optional.empty 반환)
	 */
	Optional<EssayExamQuestion> findByExamQuestionId(Long questionId);
}
