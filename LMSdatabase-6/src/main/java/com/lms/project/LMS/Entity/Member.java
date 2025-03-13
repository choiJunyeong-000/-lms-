package com.lms.project.LMS.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // 지연 로딩 관련 필드 무시
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 고유 ID
	
	private String username;  // 이 필드를 추가하세요.

	@Column(unique = true, nullable = false) // ✅ studentId를 필수값으로 변경
	private String studentId; // 학생 ID (로그인 ID로 사용)

	@Column(nullable = false)
	private String password; // 암호화된 비밀번호

	@Column(nullable = false)
	private String name; // 사용자 이름

	@Column(unique = true)  // nullable 제거 (기본값: nullable = true)
	private String email;

	@Column(nullable = false)
	private String role; // 사용자 역할 ("USER", "STUDENT", "PROFESSOR", "ADMIN")

	private String birthDate; // 생년월일 (예: "1995-06-15")
	
	

	@Builder.Default
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Student student; // 학생 정보 (STUDENT 역할일 경우)

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Professor professor; // 교수 정보 (PROFESSOR 역할일 경우)

	
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	public Member(String studentId) {
	    this.studentId = studentId;
	}
}
