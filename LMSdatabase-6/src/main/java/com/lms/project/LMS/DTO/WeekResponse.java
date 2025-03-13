package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Week;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주차 응답 DTO - 주차(Week) 정보를 클라이언트에 반환할 때 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekResponse {
    private Long id;          // id 필드 추가
    private int weekNumber;
    
    public WeekResponse(Week week) {
        this.id = week.getId();           // id 설정
        this.weekNumber = week.getWeekNumber();
    }
}