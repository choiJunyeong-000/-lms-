package com.lms.project.LMS.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lms.project.LMS.Entity.CourseQnA;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseQnADto {
    private Long id;
    private String content;
    private String title;
    private String createdAt;  // 🟢 날짜 포맷 변경 (String 타입)
    private String updatedAt;
    private String author;
    private Long authorId; 

    public CourseQnADto(CourseQnA qna) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.id = qna.getId();
        this.title = qna.getTitle();
        this.content = qna.getContent();
        this.createdAt = qna.getCreatedAt().format(formatter);  // 🟢 날짜 포맷 적용
        this.updatedAt = qna.getUpdatedAt().format(formatter);
        this.author = qna.getMember().getName();
        this.authorId = qna.getMember().getId();
    }
}