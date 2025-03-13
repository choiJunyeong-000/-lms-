package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * **통계(Statistics) 정보를 저장하는 엔티티** - 특정 강의의 다양한 통계 데이터를 저장 - 예: 출석률, 평균 성적, 시험
 * 응시율 등
 */
@Entity
@Getter
@Setter
public class Statistics {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 통계 고유 ID

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course; // 통계가 속한 강의

	private String metricName; // 통계 항목 이름 (예: "출석률", "평균 성적")

	private Double value; // 해당 통계 값 (예: 85.5는 출석률 85.5%)
}
