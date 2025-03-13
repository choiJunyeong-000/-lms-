package com.lms.project.LMS.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeeklyBoardRequest {
	private Long boardId;
    private String title;
    private int weekNumber;
    private Long courseId;
    private Long memberId;  // 추가

    // 기본 생성자
    public WeeklyBoardRequest() {
    }

    // 모든 필드를 포함한 생성자
    public WeeklyBoardRequest(Long boardId, String title, int weekNumber, Long courseId, Long memberId) {
        this.boardId = boardId;
    	this.title = title;
        this.weekNumber = weekNumber;
        this.courseId = courseId;
        this.memberId = memberId;
    }


}