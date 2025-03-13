package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * **질문(Question) 정보를 저장하는 엔티티** - 학생이 강의와 관련하여 질문한 내용을 저장 - 질문 상태(대기 중, 답변 완료
 * 등)와 답변 정보를 포함
 */
@Entity
@Getter
@Setter
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 질문 고유 ID

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course; // 질문이 속한 강의

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 질문 작성자 (학생)

	private String content; // 질문 내용

	private String answer; // 교수 또는 조교가 작성한 답변

	private String status; // 질문 상태 (예: "대기 중", "답변 완료")
}
