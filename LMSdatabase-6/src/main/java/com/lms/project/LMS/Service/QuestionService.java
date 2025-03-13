package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Question;
import com.lms.project.LMS.Repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 질문 관리 서비스 클래스
 */
@Service
public class QuestionService {

	private final QuestionRepository questionRepository;

	public QuestionService(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}

	// 모든 질문 조회
	public List<Question> getAllQuestions() {
		return questionRepository.findAll();
	}

	// 특정 강의의 질문 조회
	public List<Question> getQuestionsByCourseId(Long courseId) {
		return questionRepository.findByCourseId(courseId);
	}

	// 특정 학생의 질문 조회
	public List<Question> getQuestionsByMemberId(Long memberId) {
		return questionRepository.findByMemberId(memberId);
	}

	// 특정 상태의 질문 조회
	public List<Question> getQuestionsByStatus(String status) {
		return questionRepository.findByStatus(status);
	}

	// 질문 저장
	public Question saveQuestion(Question question) {
		return questionRepository.save(question);
	}
}
