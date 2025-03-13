package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 시험 세션 정보를 관리하는 엔티티
 * 
 * - 학생이 시험을 언제 시작하고 종료했는지 저장합니다. - 부정행위가 감지되었는지 여부를 기록합니다.
 */
@Entity
@Getter
@Setter
public class ExamSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 세션 ID

	@ManyToOne
	@JoinColumn(name = "exam_id", nullable = false)
	private Exam exam; // 어떤 시험인지 연결

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 시험을 치른 학생

	private LocalDateTime startTime; // 시험 시작 시간
	private LocalDateTime endTime; // 시험 종료 시간
	private boolean cheatingDetected; // 부정행위 감지 여부
}
