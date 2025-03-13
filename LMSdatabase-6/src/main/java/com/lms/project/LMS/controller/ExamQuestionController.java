package com.lms.project.LMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Service.ExamQuestionService;

	@RestController
	@RequestMapping("/api/exams/{examId}/questions")
	public class ExamQuestionController {

		private final ExamQuestionService questionService;

		@Autowired
		public ExamQuestionController(ExamQuestionService questionService) {
			this.questionService = questionService;
		}

    // ✅ 문제 추가 (객관식 & 서술형)
    @PostMapping
    public ResponseEntity<String> addQuestion(
            @PathVariable Long examId,
            @RequestBody ExamQuestion question) {
        
        questionService.addQuestion(examId, question);
        return ResponseEntity.ok("문제가 성공적으로 추가되었습니다.");
    }

    // ✅ 시험의 모든 문제 조회 (객관식 & 서술형 포함)
    @GetMapping
    public ResponseEntity<List<ExamQuestion>> getAllQuestions(@PathVariable Long examId) {
        List<ExamQuestion> questions = questionService.getQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    // ✅ 객관식 문제만 조회
    @GetMapping("/multiple-choice")
    public ResponseEntity<List<ExamQuestion>> getMultipleChoiceQuestions(@PathVariable Long examId) {
        List<ExamQuestion> questions = questionService.getMultipleChoiceQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    // ✅ 서술형 문제 조회
    @GetMapping("/essay")
    public ResponseEntity<List<ExamQuestion>> getEssayQuestions(@PathVariable Long examId) {
        List<ExamQuestion> questions = questionService.getEssayQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    // ✅ 서술형 문제를 EssayExamQuestion 테이블로 이동
    @PostMapping("/move-essays")
    public ResponseEntity<String> moveEssayQuestions(@PathVariable Long examId) {
        questionService.moveEssayQuestionsToEssayExamQuestions(examId);
        return ResponseEntity.ok("서술형 문제가 EssayExamQuestion으로 이동되었습니다.");
    }
    
    // ✅ 특정 문제 수정 (객관식 & 서술형)
    @PutMapping("/{questionId}")
    public ResponseEntity<String> updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @RequestBody ExamQuestion updatedQuestion) {

        boolean isUpdated = questionService.updateQuestion(examId, questionId, updatedQuestion);

        if (isUpdated) {
            return ResponseEntity.ok("문제가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("문제 수정에 실패했습니다.");
        }
    }
    
    // ✅ 답변 저장
    @PostMapping("/submit")
    public ResponseEntity<String> submitAnswers(
            @PathVariable Long examId,
            @RequestBody Map<String, Object> request) {
        
        String studentId = request.get("studentId").toString();
        Map<String, String> answers = (Map<String, String>) request.get("answers");
        
        questionService.saveAnswers(examId, studentId, answers);
        return ResponseEntity.ok("답변이 성공적으로 제출되었습니다.");
    }
    // ✅ 제출 상태 확인
    @GetMapping("/submission-status")
    public ResponseEntity<Boolean> getSubmissionStatus(
            @PathVariable Long examId,
            @RequestParam String studentId) {
        
        boolean isSubmitted = questionService.isSubmitted(examId, studentId);
        return ResponseEntity.ok(isSubmitted);
    }
}
