package com.lms.project.LMS.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 고유 ID

	@OneToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	@JsonBackReference
	private Member member; // Member와 연결
	

	@Enumerated(EnumType.STRING)
	private Status status; // 교수 상태 (PENDING, APPROVED)

	public enum Status {
		PENDING, APPROVED // 교수 상태 값 (승인 대기 중, 승인됨)
	}
}
