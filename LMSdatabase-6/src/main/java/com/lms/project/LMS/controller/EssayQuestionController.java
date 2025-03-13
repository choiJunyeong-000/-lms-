package com.lms.project.LMS.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.project.LMS.DTO.EssayExamQuestionDTO;
import com.lms.project.LMS.Entity.EssayExamQuestion;
import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Entity.StudentAnswer;
import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Enum.ExamQuestionType;
import com.lms.project.LMS.Service.EssayExamQuestionService;
import com.lms.project.LMS.Repository.ExamQuestionRepository;
import com.lms.project.LMS.Repository.StudentAnswerRepository;
import com.lms.project.LMS.Repository.SubmissionRepository;

@RestController
@RequestMapping("/api/essay-questions")
public class EssayQuestionController {

    private final EssayExamQuestionService essayQuestionService;
    private final SubmissionRepository submissionRepository; // 🔹 추가된 부분
    private final StudentAnswerRepository studentAnswerRepository;
    private final ExamQuestionRepository examQuestionRepository;
    @Autowired
    public EssayQuestionController(EssayExamQuestionService essayQuestionService, 
                                   SubmissionRepository submissionRepository,
                                   StudentAnswerRepository studentAnswerRepository,
                                   ExamQuestionRepository examQuestionRepository) { // 🔹 생성자에 추가
        this.essayQuestionService = essayQuestionService;
        this.submissionRepository = submissionRepository;
        this.studentAnswerRepository=studentAnswerRepository;
        this.examQuestionRepository=examQuestionRepository;
    }


    // ESSAY 타입 질문으로 서술형 문제 생성
    @PostMapping("/create-from-exam/{examId}")
    public ResponseEntity<String> createEssayQuestions(@PathVariable Long examId) {
        essayQuestionService.createEssayQuestionsFromExamId(examId);
        return ResponseEntity.ok("서술형 문제가 성공적으로 생성되었습니다.");
    }

    // 서술형 문제 추가
    @PostMapping
    public ResponseEntity<String> addEssayAnswer(@RequestBody EssayExamQuestion essayExamQuestion) {  // 변경된 클래스명
        essayQuestionService.addEssayAnswer(essayExamQuestion);
        return ResponseEntity.ok("서술형 답변이 성공적으로 추가되었습니다.");
    }

    // 특정 서술형 문제 조회 (ExamQuestion ID로)
    @GetMapping("/question/{questionId}")
    public ResponseEntity<EssayExamQuestionDTO> getEssayAnswerByQuestionId(@PathVariable Long questionId) {
        EssayExamQuestionDTO essayQuestion = essayQuestionService.getEssayQuestionByQuestionId(questionId);
        return ResponseEntity.ok(essayQuestion);
    }

    // 교수님이 점수 부여
    @PutMapping("/score/{essayQuestionId}")
    public ResponseEntity<String> updateScore(@PathVariable Long essayQuestionId, @RequestBody Integer score) {
        essayQuestionService.updateScore(essayQuestionId, score);
        return ResponseEntity.ok("점수가 업데이트되었습니다.");
    }

    // 서술형 답변 업데이트
    @PutMapping("/{essayQuestionId}/answer")
    public ResponseEntity<String> updateEssayAnswer(@PathVariable Long essayQuestionId, @RequestBody String studentAnswer) {
        essayQuestionService.updateEssayAnswer(essayQuestionId, studentAnswer);
        return ResponseEntity.ok("서술형 답변이 성공적으로 업데이트되었습니다.");
    }

 // 시험에 제출한 모든 서술형 질문 답변 조회
    @GetMapping("/exam/{examId}/answers")
    public ResponseEntity<List<Map<String, Object>>> getEssayAnswersByExam(@PathVariable Long examId) {
        // 1️⃣ 서술형 질문만 가져오기 (ExamQuestion에서 type이 ESSAY인 질문 필터링)
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdAndType(examId, ExamQuestionType.ESSAY);
        
        // 2️⃣ 서술형 질문에 대한 답변을 찾아서 가져오기
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByExamIdAndQuestionIn(examId, examQuestions);

        // 3️⃣ 답변을 Map 형태로 변환
        List<Map<String, Object>> essayAnswers = new ArrayList<>();
        
        for (StudentAnswer answer : studentAnswers) {
            Map<String, Object> answerData = new HashMap<>();
            answerData.put("studentId", answer.getStudent().getId());
            answerData.put("studentName", answer.getStudent().getName());
            answerData.put("questionId", answer.getQuestion().getId());
            answerData.put("questionText", answer.getQuestion().getQuestionText());
            answerData.put("answerText", answer.getAnswer());
            essayAnswers.add(answerData);
        }

        return ResponseEntity.ok(essayAnswers);
    }


    
 // 서술형 답변 채점 (학생 ID + 문제 ID 기준)
    @PutMapping("/score")
    public ResponseEntity<String> updateEssayScore(@RequestBody Map<String, Object> gradingRequest) {
        // 기본값 설정
        Long studentId = gradingRequest.containsKey("studentId") ? ((Number) gradingRequest.get("studentId")).longValue() : null;
        Long questionId = gradingRequest.containsKey("questionId") ? ((Number) gradingRequest.get("questionId")).longValue() : null;
        int score = gradingRequest.containsKey("score") && gradingRequest.get("score") != null 
                    ? ((Number) gradingRequest.get("score")).intValue() 
                    : 0;  // 기본값 0

        // 1️⃣ 해당 학생의 답변 찾기
        Optional<StudentAnswer> optionalAnswer = studentAnswerRepository.findByStudentIdAndQuestionId(studentId, questionId);
        if (optionalAnswer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 학생의 답변을 찾을 수 없습니다.");
        }

        // 2️⃣ 답변 객체 업데이트 (기본값으로 점수 업데이트)
        StudentAnswer studentAnswer = optionalAnswer.get();
        if (score != 0) { // score가 있을 경우만 업데이트
            studentAnswer.setScore(score);
        }
        studentAnswerRepository.save(studentAnswer);

        // 3️⃣ 해당 학생의 모든 점수를 가져와서 합산 (객관식 + 서술형)
        int totalScore = studentAnswerRepository.findByStudentId(studentId)
            .stream()
            .mapToInt(sa -> sa.getScore() != null ? sa.getScore() : 0) // 점수 합산
            .sum();

        // 4️⃣ submission 테이블 업데이트
        Optional<Submission> submissionOpt = submissionRepository.findByMemberIdAndExamId(studentId, studentAnswer.getExam().getId());
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();
            submission.setTotalScore(totalScore); // 🔹 계산된 총점 저장
            submissionRepository.save(submission);
        }

        return ResponseEntity.ok("서술형 답변 채점 완료 및 총점 업데이트됨");
    }








}
