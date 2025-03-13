package com.lms.project.LMS.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EssayExamQuestionDTO {
	private Long id;

	@NotNull
	private Long examQuestionId; // ExamQuestion의 ID (questionId → examQuestionId로 변경)

	@NotNull
	private String questionText; // 질문 텍스트

	private String studentAnswer; // 학생이 작성한 답변 (answer → studentAnswer로 변경)

	private Integer score; // 교수님이 입력한 점수

	// 🔹 추가된 메서드 1: setAnswer
	public void setAnswer(String answer) {
		this.studentAnswer = answer; // 기존 studentAnswer와 동일하게 설정
	}

	// 🔹 추가된 메서드 2: setQuestionId
	public void setQuestionId(Long questionId) {
		this.examQuestionId = questionId;
	}
}
