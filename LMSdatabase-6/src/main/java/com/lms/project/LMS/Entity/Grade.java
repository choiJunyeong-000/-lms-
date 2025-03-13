package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 성적(Grade) 정보를 관리하는 엔티티 - 학생(Member)이 특정 강의(Course)에서 받은 성적을 저장합니다. -
 * 교수(Member)가 입력한 성적을 저장합니다.
 */
@Entity
@Getter
@Setter
public class Grade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 성적 고유 ID

	@ManyToOne
	@JoinColumn(name = "student_id", nullable = false)
	private Member student; // 성적을 받은 학생

	@ManyToOne
	@JoinColumn(name = "professor_id", nullable = false)
	private Member professor; // 성적을 입력한 교수

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course; // 성적이 속한 강의

	@Column(nullable = false)
	private Double score; // 성적 점수 (Float → Double로 변경)

	private String remarks; // 성적에 대한 추가적인 설명 (예: "우수한 성적")
	private String status; // 성적 상태 (예: "확인됨", "미제출")

	// ❌ 불필요한 `studentName` 필드를 제거함.
}
