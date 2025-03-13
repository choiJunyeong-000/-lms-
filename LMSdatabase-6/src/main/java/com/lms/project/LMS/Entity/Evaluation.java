package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 시험 평가 정보를 저장하는 엔티티
 * 
 * - 특정 시험에서 학생의 점수를 저장 - 학생(Member)와 시험(Exam) 간의 관계를 정의
 */
@Entity
@Getter
@Setter
public class Evaluation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 평가 고유 ID

	@ManyToOne
	@JoinColumn(name = "exam_id", nullable = false)
	private Exam exam; // 평가가 속한 시험

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 평가를 받은 학생 (Member 테이블과 연결)

	private Double score; // 학생의 점수 (예: 95.5)
}
