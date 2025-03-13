package com.lms.project.LMS.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class QuizSubmissionRequest {
    private Long courseId; // 강의 ID
    private Map<Long, String> answers; // 문제 ID별 학생의 답변
}