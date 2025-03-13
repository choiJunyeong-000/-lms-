package com.lms.project.LMS.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAttendanceDTO {
    private Long weekId;  // 주차 (1~15주차)
    private String fileName;  // 강의명
    private double progress;  // 진행률 (%)
    private boolean present;  // 출석 여부
}
