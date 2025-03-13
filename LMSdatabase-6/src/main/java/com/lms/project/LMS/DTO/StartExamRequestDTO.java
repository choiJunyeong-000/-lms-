package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.Member;
import lombok.Data;

/**
 * 시험 시작 요청을 위한 DTO
 * 
 * - 특정 시험을 학생이 시작할 때 필요한 정보를 전달
 */
@Data
public class StartExamRequestDTO {

	private Exam exam; // 시험 엔티티 참조
	private Member student; // 학생(Member 엔티티) 참조

	/**
	 * 기존 팀원 코드에서 examId, studentId를 사용했으나, 객체 참조 방식으로 변경하여 연관된 엔티티를 직접 다룰 수 있도록 개선
	 */
}
