package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 가상 강의 정보를 관리하는 엔티티 - 강의와 연계된 가상 강의 세부 정보를 저장합니다. - 강의를 생성한 교수(Member)와
 * 강의(Course)를 연계하여 관리합니다.
 */
@Entity
@Getter
@Setter
public class VirtualLecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 가상 강의 고유 ID

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course; // 가상 강의가 속한 강의 (Course 테이블 참조)

	private String title; // 가상 강의 제목

	private String meetingLink; // 가상 강의 링크 (예: Zoom, Google Meet 링크)

	private LocalDateTime scheduledAt; // 가상 강의 예정 시간

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false) // 교수와의 관계 설정
	private Member member; // 강의를 생성한 교수 (Member 테이블 참조)
}
