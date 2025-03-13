package com.lms.project.LMS.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 강의 피드백 요청 DTO - 학생이 강의에 대한 피드백을 제출할 때 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
	private Long studentId; // 학생 ID
	private Long courseId; // 강의 ID
	private String content; // 피드백 내용
	private int rating; // 평점 (1~5)
	private String status; // 피드백 상태 (예: "승인", "대기", "수정 필요")
}
