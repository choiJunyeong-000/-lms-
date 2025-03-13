package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Survey;
import com.lms.project.LMS.Entity.SurveyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDTO {
    private Long id;
    private String title;
    private String description;
    private String options;
    private boolean isActive;
    private SurveyType surveyType;
    private Long courseId;
    private LocalDateTime startDate;  // 추가됨
    private LocalDateTime endDate;    // 추가됨

    public static SurveyDTO fromEntity(Survey survey) {
        return new SurveyDTO(
            survey.getId(),
            survey.getTitle(),
            survey.getDescription(),
            survey.getOptions(),
            survey.isActive(),
            survey.getSurveyType(),
            (survey.getCourse() != null) ? survey.getCourse().getId() : null,
            survey.getStartDate(),  // 추가됨
            survey.getEndDate()     // 추가됨
        );
    }
}
