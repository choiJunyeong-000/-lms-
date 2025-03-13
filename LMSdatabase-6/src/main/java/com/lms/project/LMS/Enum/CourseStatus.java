package com.lms.project.LMS.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CourseStatus {
	OPEN("OPEN", "진행중"), // 진행 중
	COMPLETED("COMPLETED", "완료"), // 완료됨
	CANCELLED("CANCELLED", "취소"); // 취소됨

	private final String value;
	private final String statusInKorean;

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static CourseStatus fromString(String status) {
		for (CourseStatus courseStatus : CourseStatus.values()) {
			// 한글 상태 값도 처리 (예: "진행중" 입력 시 OPEN으로 변환)
			if (courseStatus.statusInKorean.equalsIgnoreCase(status)) {
				return courseStatus;
			}
			// 기존 영어 값도 처리 (예: "OPEN" 입력 시 OPEN으로 변환)
			if (courseStatus.value.equalsIgnoreCase(status)) {
				return courseStatus;
			}
		}
		throw new IllegalArgumentException("Unknown status: " + status);
	}
}
