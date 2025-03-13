package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 강의 피드백 정보를 관리하는 엔티티 - 학생이 강의에 대해 남긴 피드백을 저장 - 내용, 평점, 상태 등을 관리
 */
@Entity
@Getter
@Setter
public class Feedback {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 피드백 고유 ID

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course; // 피드백이 속한 강의 (Course 테이블과 연결)

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 피드백을 작성한 학생 (Member 테이블과 연결)

	private String content; // 피드백 내용
	private int rating; // 평점 (예: 1~5점)
	private String status; // 피드백 상태 (예: "승인", "대기", "수정 필요")
	private boolean isEditable = true; // 피드백 수정 가능 여부 (기본값: 가능)

	// 기본 생성자
	public Feedback() {
	}

	// 필드 초기화 생성자 (강의, 학생, 피드백 내용, 평점 등)
	public Feedback(Course course, Member member, String content, int rating, String status) {
		this.course = course;
		this.member = member;
		this.content = content;
		this.rating = rating;
		this.status = status;
	}

	// 모든 필드를 초기화하는 생성자
	public Feedback(Long id, Course course, Member member, String content, int rating, String status,
			boolean isEditable) {
		this.id = id;
		this.course = course;
		this.member = member;
		this.content = content;
		this.rating = rating;
		this.status = status;
		this.isEditable = isEditable;
	}

	// 복사 생성자
	public Feedback(Feedback feedback) {
		this.id = feedback.getId();
		this.course = feedback.getCourse();
		this.member = feedback.getMember();
		this.content = feedback.getContent();
		this.rating = feedback.getRating();
		this.status = feedback.getStatus();
		this.isEditable = feedback.isEditable();
	}
}
