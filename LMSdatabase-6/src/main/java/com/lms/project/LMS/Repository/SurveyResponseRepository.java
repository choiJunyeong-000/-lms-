package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 설문 응답 데이터를 관리하는 리포지토리
 */
@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    // 특정 설문에 대한 응답 조회
    List<SurveyResponse> findBySurveyId(Long surveyId);

    // 특정 학생의 응답 조회
    List<SurveyResponse> findByMemberId(Long memberId);

    // 설문 ID와 학생 ID로 중복 응답을 조회
    SurveyResponse findBySurveyIdAndMemberId(Long surveyId, Long memberId);
}
