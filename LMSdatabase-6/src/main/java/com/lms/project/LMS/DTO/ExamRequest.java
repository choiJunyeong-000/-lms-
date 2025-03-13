package com.lms.project.LMS.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class ExamRequest {
    private String title;         // 시험 제목
    private String description;   // 시험 설명
    private Date date;            // 시험 날짜
    private Double totalPoints;   // ✅ 시험 총점 추가
    private String examType;      // ✅ 시험 유형 추가 (예: 중간고사, 기말고사)
    private Long professorId;     // 교수 ID
    private Long courseId;        // 강의 ID
}
