package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.SurveyTemplate;
import com.lms.project.LMS.Repository.SurveyTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyTemplateService {

    private final SurveyTemplateRepository surveyTemplateRepository;

    public SurveyTemplateService(SurveyTemplateRepository surveyTemplateRepository) {
        this.surveyTemplateRepository = surveyTemplateRepository;
    }

    // ✅ 활성화된 공통 설문 가져오기
    public List<SurveyTemplate> getActiveSurveyTemplates() {
        return surveyTemplateRepository.findByIsActiveTrue();
    }

    // ✅ 새로운 공통 설문 추가
    public SurveyTemplate createSurveyTemplate(SurveyTemplate surveyTemplate) {
        return surveyTemplateRepository.save(surveyTemplate);
    }

    // ✅ 공통 설문 비활성화 (삭제 대신 비활성화)
    public void deactivateSurveyTemplate(Long id) {
        SurveyTemplate surveyTemplate = surveyTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("설문 템플릿을 찾을 수 없습니다."));
        surveyTemplate.setActive(false);
        surveyTemplateRepository.save(surveyTemplate);
    }
}
