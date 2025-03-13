package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 활동 로그를 관리하는 엔티티
 * 
 * - 사용자의 활동 내역을 기록하는 테이블 - 로그인, 강의 등록, 과제 제출 등의 활동을 저장
 */
@Entity
@Getter
@Setter
public class ActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 활동 로그 고유 ID

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = true)
	private Member member; // Member 엔티티와의 연관 관계 (사용자 ID)

	private String action; // 수행된 활동 (예: "로그인", "강의 등록", "과제 제출")

	private LocalDateTime timestamp; // 활동이 발생한 시간
}
