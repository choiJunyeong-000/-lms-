package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
public class QnAAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "qna_id", nullable = false)
    @JsonIgnore
    private CourseQnA courseQnA; // ✅ QnA와 연결

 
    @ManyToOne(fetch = FetchType.LAZY) // ✅ Lazy 로딩 추가
    @JoinColumn(name = "member_id", nullable = false)
    
    private Member author; // ✅ 작성자

    @Column(nullable = false)
    private String content; // ✅ 답변 내용

    private LocalDateTime createdAt = LocalDateTime.now();
}

