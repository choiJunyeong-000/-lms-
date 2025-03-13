package com.lms.project.LMS.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Service.GradingService;

@RestController
@RequestMapping("/api/grade")
public class GradingController {
    
    @Autowired
    private GradingService gradingService;

    @PostMapping("/exam")
    public ResponseEntity<String> gradeExam(@RequestBody Map<String, Object> requestBody) {
        try {
            // 🔹 String으로 들어오는 값을 Long으로 변환
            Long studentId = Long.parseLong(requestBody.get("studentId").toString());
            Long examId = Long.parseLong(requestBody.get("examId").toString());

            Map<Long, String> answers = new HashMap<>();
            Map<String, String> answersRaw = (Map<String, String>) requestBody.get("answers");

            // 🔹 answers 맵의 key(String) → Long 변환
            for (Map.Entry<String, String> entry : answersRaw.entrySet()) {
                answers.put(Long.parseLong(entry.getKey()), entry.getValue());
            }

            gradingService.gradeExam(studentId, examId, answers);
            return ResponseEntity.ok("✅ 자동 채점 완료");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ 요청 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/essay-score")
    public ResponseEntity<String> gradeEssayScores(@RequestBody Submission submission) {
        gradingService.saveEssayScores(submission);
        return ResponseEntity.ok("서술형 문제 점수가 성공적으로 입력되었습니다.");
    }
}
