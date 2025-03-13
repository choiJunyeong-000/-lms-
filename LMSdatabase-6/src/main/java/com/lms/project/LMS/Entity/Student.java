package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 고유 ID

	@OneToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	@JsonBackReference
	private Member member; // Member와 연결

	@Enumerated(EnumType.STRING)
	private Status status; // 학생 상태 (PENDING, APPROVED)

	@ManyToMany
	@JoinTable(name = "enrollment", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course> courses; // 수강한 강의 목록

	public enum Status {
		PENDING, APPROVED // 학생 상태 값 (승인 대기 중, 승인됨)
	}
	
	
}
