package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Survey;
import com.lms.project.LMS.Entity.SurveyResponse;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.SurveyRepository;
import com.lms.project.LMS.Repository.SurveyResponseRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;

    public SurveyResponseService(SurveyResponseRepository surveyResponseRepository,
                                 SurveyRepository surveyRepository,
                                 MemberRepository memberRepository) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyRepository = surveyRepository;
        this.memberRepository = memberRepository;
    }

    // 📌 중복 설문 제출 체크
    public boolean checkDuplicateSubmission(Long surveyId, Long memberId) {
        // surveyId와 memberId로 중복된 응답이 있는지 확인
        SurveyResponse existingResponse = surveyResponseRepository.findBySurveyIdAndMemberId(surveyId, memberId);
        return existingResponse != null; // 이미 존재하면 중복으로 간주
    }

    // 📌 특정 설문에 대한 응답 조회
    public List<SurveyResponse> getResponsesBySurveyId(Long surveyId) {
        return surveyResponseRepository.findBySurveyId(surveyId);
    }

    // 📌 특정 학생의 응답 조회
    public List<SurveyResponse> getResponsesByMemberId(Long memberId) {
        return surveyResponseRepository.findByMemberId(memberId);
    }

    // 📌 설문 응답 저장 (Survey 및 Member 객체 변환 추가)
    public void saveResponse(Long surveyId, Long memberId, String responseText) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new IllegalArgumentException("설문이 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setSurvey(survey);
        surveyResponse.setMember(member);
        surveyResponse.setResponse(responseText);

        surveyResponseRepository.save(surveyResponse);
    }
}
