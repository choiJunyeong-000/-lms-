package com.lms.project.LMS.DTO;

import java.time.LocalDateTime;

import com.lms.project.LMS.Entity.QnAAnswer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가
public class QnAAnswerRequest {
	 private Long id;
	    private String content;
	    private LocalDateTime createdAt;
	    private String authorName; // Member의 username 포함
	    private Long authorId;
	    private String message;

	    public QnAAnswerRequest(QnAAnswer answer) {
	        this.id = answer.getId();
	        this.content = answer.getContent();
	        this.createdAt = answer.getCreatedAt();
	        this.authorName = answer.getAuthor() != null ? answer.getAuthor().getName() : "알 수 없";
	        this.authorId = answer.getAuthor().getId(); 
	    }
	    public QnAAnswerRequest(String message) {
	        this.message = message;
	    }

	
}
