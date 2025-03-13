package com.lms.project.LMS.controller;

import com.lms.project.LMS.DTO.SurveyDTO;
import com.lms.project.LMS.Service.SurveyService;
import com.lms.project.LMS.Entity.SurveyType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // 📌 모든 설문 조회 (DTO 반환)
    @GetMapping
    public ResponseEntity<List<SurveyDTO>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    // 📌 특정 강의의 강의 평가 설문 조회 (DTO 반환)
    @GetMapping("/lecture/{courseId}")
    public ResponseEntity<List<SurveyDTO>> getLectureSurveys(@PathVariable Long courseId) {
        return ResponseEntity.ok(surveyService.getSurveysByCourseId(courseId));
    }

    // 📌 특정 설문 유형 조회 (DTO 반환)
    @GetMapping("/type/{surveyType}")
    public ResponseEntity<List<SurveyDTO>> getSurveysByType(@PathVariable SurveyType surveyType) {
        return ResponseEntity.ok(surveyService.getSurveysByType(surveyType));
    }

   

    // 📌 활성화된 설문 조회 (DTO 반환)
    @GetMapping("/active")
    public ResponseEntity<List<SurveyDTO>> getActiveSurveys() {
        return ResponseEntity.ok(surveyService.getActiveSurveys());
    }

    // 📌 특정 설문 조회 (DTO 반환)
    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyDTO> getSurveyById(@PathVariable Long surveyId) {
        SurveyDTO survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(survey);
    }

    @PostMapping("/addsurvey")
    public ResponseEntity<SurveyDTO> createSurvey(@RequestBody SurveyDTO surveyDTO) {
        SurveyDTO createdSurvey = surveyService.createSurvey(surveyDTO);
        return ResponseEntity.ok(createdSurvey);
    }

}
