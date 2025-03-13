package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 설문 응답 정보를 관리하는 테이블
 * 
 * - 학생이 특정 설문에 제출한 응답을 저장합니다. - 설문(Survey) 테이블과 학생(Member) 테이블을 연계하여 응답 데이터를
 * 관리합니다.
 */
@Entity
@Getter
@Setter
public class SurveyResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 설문 응답 고유 ID

	@ManyToOne
	@JoinColumn(name = "survey_id", nullable = false)
	private Survey survey; // 설문 ID (Survey 테이블 참조)

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 응답자 ID (Member 테이블 참조)

	private String response; // 설문 응답 내용 (학생이 제출한 답변)
}
