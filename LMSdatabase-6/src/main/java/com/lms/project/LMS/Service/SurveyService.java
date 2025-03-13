package com.lms.project.LMS.Service;

import com.lms.project.LMS.DTO.SurveyDTO;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Survey;
import com.lms.project.LMS.Entity.SurveyType;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.SurveyRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final CourseRepository courseRepository;
    

    public SurveyService(SurveyRepository surveyRepository, CourseRepository courseRepository) {
        this.surveyRepository = surveyRepository;
        this.courseRepository = courseRepository;
        
    }

    // ✅ 모든 설문 조회 (기존 기능 유지)
    public List<SurveyDTO> getAllSurveys() {
        return surveyRepository.findAll()
                .stream()
                .map(SurveyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 특정 강의의 설문 조회 (기존 기능 유지)
    public List<SurveyDTO> getSurveysByCourseId(Long courseId) {
        return surveyRepository.findLectureSurveysByCourseId(courseId)
                .stream()
                .map(SurveyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 특정 설문 유형 조회 (기존 기능 유지)
    public List<SurveyDTO> getSurveysByType(SurveyType surveyType) {
        return surveyRepository.findBySurveyType(surveyType)
                .stream()
                .map(SurveyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    

    // ✅ 활성화된 설문 조회 (기존 기능 유지)
    public List<SurveyDTO> getActiveSurveys() {
        return surveyRepository.findByIsActiveTrue()
                .stream()
                .map(SurveyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 특정 설문 ID로 조회 (기존 기능 유지)
    public SurveyDTO getSurveyById(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .map(SurveyDTO::fromEntity)
                .orElse(null);
    }

    // ✅ **설문 등록 기능 추가!**
    public SurveyDTO createSurvey(SurveyDTO surveyDTO) {
        Survey survey = new Survey();
        survey.setTitle(surveyDTO.getTitle());
        survey.setDescription(surveyDTO.getDescription());
        survey.setOptions(surveyDTO.getOptions());
        survey.setActive(surveyDTO.isActive());
        survey.setSurveyType(surveyDTO.getSurveyType());

        // ✅ startDate, endDate 설정 추가
     // startDate, endDate가 null이면 기본값 설정
        survey.setStartDate(surveyDTO.getStartDate() != null ? surveyDTO.getStartDate() : LocalDateTime.now());
        survey.setEndDate(surveyDTO.getEndDate() != null ? surveyDTO.getEndDate() : LocalDateTime.now().plusWeeks(2));


        if (surveyDTO.getCourseId() != null) {
            Course course = courseRepository.findById(surveyDTO.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 강의가 존재하지 않습니다."));
            survey.setCourse(course);
        }

        survey = surveyRepository.save(survey);
        return SurveyDTO.fromEntity(survey);
    }


}
