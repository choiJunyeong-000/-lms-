package com.lms.project.LMS.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Enum.ExamQuestionType;

/**
 * 시험 문제(ExamQuestion) 데이터를 관리하는 리포지토리 - 기본적인 CRUD 기능 제공 (JpaRepository 자동 포함) -
 * 시험 ID 또는 문제 유형을 기준으로 데이터 조회 기능 추가
 */
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

	/**
	 * 특정 시험(examId)에 속한 모든 문제 조회
	 * 
	 * @param examId 시험 ID
	 * @return 해당 시험에 속한 모든 문제 리스트
	 */
	List<ExamQuestion> findByExamId(Long examId);

	/**
	 * 특정 시험(examId)과 문제 유형(type)에 해당하는 문제 조회
	 * 
	 * @param examId 시험 ID
	 * @param type   문제 유형 (객관식, 서술형 등)
	 * @return 해당 시험 및 유형에 맞는 문제 리스트
	 */
	List<ExamQuestion> findByExamIdAndType(Long examId, ExamQuestionType type);
}
