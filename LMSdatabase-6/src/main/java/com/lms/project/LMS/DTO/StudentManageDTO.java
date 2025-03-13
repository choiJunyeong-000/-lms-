package com.lms.project.LMS.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentManageDTO {
    private Long courseId;
    private String courseName;
    private String studentName;
    private String studentId;
    private double attendanceRate; // 출석률
}
