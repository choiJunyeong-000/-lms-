package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Survey;
import com.lms.project.LMS.Entity.SurveyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    // ✅ 특정 강의(courseId)의 강의 평가 설문 조회
    @Query("SELECT s FROM Survey s WHERE s.course.id = :courseId AND s.surveyType = 'LECTURE_EVALUATION'")
    List<Survey> findLectureSurveysByCourseId(@Param("courseId") Long courseId);

    // ✅ 특정 설문 유형 전체 조회 (예: 급식 설문, 학사 운영 설문)
    List<Survey> findBySurveyType(SurveyType surveyType);

    // ✅ 활성화된 설문 조회 (isActive = true)
    List<Survey> findByIsActiveTrue();

    
}
