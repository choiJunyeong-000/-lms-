package com.lms.project.LMS.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.Evaluation;
import com.lms.project.LMS.Repository.EvaluationRepository;
import java.util.List;

/**
 * 평가(Evaluation) 데이터를 관리하는 서비스 클래스 - 기본적인 CRUD 기능 제공
 */
@Service
public class EvaluationService {

	private final EvaluationRepository evaluationRepository;

	@Autowired
	public EvaluationService(EvaluationRepository evaluationRepository) {
		this.evaluationRepository = evaluationRepository;
	}

	/**
	 * 모든 평가 조회
	 * 
	 * @return 평가 목록
	 */
	public List<Evaluation> getAllEvaluations() {
		return evaluationRepository.findAll();
	}

	/**
	 * 평가 저장
	 * 
	 * @param evaluation 평가 데이터
	 * @return 저장된 평가 데이터
	 */
	public Evaluation saveEvaluation(Evaluation evaluation) {
		return evaluationRepository.save(evaluation);
	}
}
