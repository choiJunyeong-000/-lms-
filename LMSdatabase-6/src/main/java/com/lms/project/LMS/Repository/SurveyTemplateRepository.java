package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.SurveyTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SurveyTemplateRepository extends JpaRepository<SurveyTemplate, Long> {
    List<SurveyTemplate> findByIsActiveTrue(); // 활성화된 설문만 가져오기
}
