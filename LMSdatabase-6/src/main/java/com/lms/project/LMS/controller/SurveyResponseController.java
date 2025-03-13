package com.lms.project.LMS.controller;

import com.lms.project.LMS.Entity.SurveyResponse;
import com.lms.project.LMS.Service.SurveyResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/survey-responses")
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;

    public SurveyResponseController(SurveyResponseService surveyResponseService) {
        this.surveyResponseService = surveyResponseService;
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkSurveyResponse(@RequestBody Map<String, Object> payload) {
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        Long surveyId = Long.valueOf(payload.get("surveyId").toString());

        System.out.println("Received check request: surveyId=" + surveyId + ", memberId=" + memberId);  // 로그 추가

        boolean isDuplicate = surveyResponseService.checkDuplicateSubmission(surveyId, memberId);

        if (isDuplicate) {
            return ResponseEntity.status(400).body("이미 설문을 제출한 기록이 있습니다.");
        }

        return ResponseEntity.ok("설문을 제출할 수 있습니다.");
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitSurveyResponse(@RequestBody Map<String, Object> payload) {
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        List<Map<String, String>> responses = (List<Map<String, String>>) payload.get("responses");

        // responses 배열을 순회하며 각 설문에 대한 응답을 저장합니다.
        for (Map<String, String> response : responses) {
            Long surveyId = Long.valueOf(response.get("surveyId"));
            String responseText = response.get("response");

            // 각 응답을 저장하는 서비스 호출
            surveyResponseService.saveResponse(surveyId, memberId, responseText);
        }

        return ResponseEntity.ok("✅ 설문 응답이 저장되었습니다!");
    }

    // 📌 특정 설문에 대한 응답 조회 API (설문 ID 기준)
    @GetMapping("/{surveyId}")
    public ResponseEntity<List<SurveyResponse>> getSurveyResponses(@PathVariable Long surveyId) {
        List<SurveyResponse> responses = surveyResponseService.getResponsesBySurveyId(surveyId);
        return ResponseEntity.ok(responses);
    }

    // 📌 특정 학생의 설문 응답 조회 API (학생 ID 기준)
    @GetMapping("/student/{memberId}")
    public ResponseEntity<List<SurveyResponse>> getStudentResponses(@PathVariable Long memberId) {
        List<SurveyResponse> responses = surveyResponseService.getResponsesByMemberId(memberId);
        return ResponseEntity.ok(responses);
    }
}
