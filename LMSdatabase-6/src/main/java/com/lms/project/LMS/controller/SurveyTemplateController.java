package com.lms.project.LMS.controller;

import com.lms.project.LMS.Entity.SurveyTemplate;
import com.lms.project.LMS.Service.SurveyTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys/templates")
public class SurveyTemplateController {

    private final SurveyTemplateService surveyTemplateService;

    public SurveyTemplateController(SurveyTemplateService surveyTemplateService) {
        this.surveyTemplateService = surveyTemplateService;
    }

    // 📌 1️⃣ 활성화된 공통 설문 가져오기
    @GetMapping
    public ResponseEntity<List<SurveyTemplate>> getSurveyTemplates() {
        List<SurveyTemplate> templates = surveyTemplateService.getActiveSurveyTemplates();
        return ResponseEntity.ok(templates);
    }

    // 📌 2️⃣ 새로운 공통 설문 추가
    @PostMapping
    public ResponseEntity<SurveyTemplate> createSurveyTemplate(@RequestBody SurveyTemplate surveyTemplate) {
        SurveyTemplate createdSurveyTemplate = surveyTemplateService.createSurveyTemplate(surveyTemplate);
        return ResponseEntity.ok(createdSurveyTemplate);
    }

    // 📌 3️⃣ 설문 비활성화
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateSurveyTemplate(@PathVariable Long id) {
        surveyTemplateService.deactivateSurveyTemplate(id);
        return ResponseEntity.ok("✅ 설문 템플릿이 비활성화되었습니다.");
    }
}
