package com.lms.project.LMS.DTO;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 등록 및 로그인 시 데이터를 전달하는 DTO. PM 설계를 기반으로 데이터베이스 중심 구조로 변경됨.
 */
@Data
public class MemberDTO {

	@NotBlank
	private Long id; // 데이터베이스 고유 식별자 (Long으로 수정)

	@NotBlank
	private String username; // 로그인 ID

	@NotBlank
	private String name; // 사용자 이름

	@NotBlank
	private String password; // 사용자 비밀번호

	@NotBlank
	@Email
	private String email; // 사용자 이메일

	// 생년월일 필드 추가
	private String birthDate; // 생년월일 (예: "1995-06-15")

	// 🔹 getBirthDate 메서드 추가
	public String getBirthDate() {
		return birthDate;
	}

	// 🔹 setBirthDate 메서드 추가
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
}
