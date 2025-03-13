package com.lms.project.LMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.project.LMS.Entity.Evaluation;
import com.lms.project.LMS.Service.EvaluationService;

// 평가 관련 HTTP 요청을 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

	@Autowired
	private EvaluationService evaluationService; // EvaluationService 의존성 주입

	// 모든 평가 정보를 조회하는 GET 요청 처리
	@GetMapping
	public ResponseEntity<List<Evaluation>> getAllEvaluations() {
		List<Evaluation> evaluations = evaluationService.getAllEvaluations(); // 평가 목록 가져오기
		return ResponseEntity.ok(evaluations); // 평가 목록 반환
	}

	// 평가 정보를 생성하는 POST 요청 처리
	@PostMapping
	public ResponseEntity<Evaluation> createEvaluation(@RequestBody Evaluation evaluation) {
		Evaluation createdEvaluation = evaluationService.saveEvaluation(evaluation); // 평가 정보 생성
		return ResponseEntity.ok(createdEvaluation); // 생성된 평가 반환
	}
}
