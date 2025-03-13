package com.lms.project.LMS.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 강좌별 Q&A 게시판 엔티티
 */
@Entity
@Getter
@Setter
public class CourseQnA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Q&A ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // 강좌 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 작성자 연관 관계

    private String title; // 제목
    
    @Column(columnDefinition = "TEXT")
    private String content; // 질문 내용

    @Column(columnDefinition = "TEXT")
    private String answer; // **교수의 답변 추가**

    private boolean isSecret; // 비밀글 여부 (isPrivate -> isSecret로 수정)
    
    private LocalDateTime createdAt; // 생성 날짜
    
    private LocalDateTime updatedAt; // 수정 날짜
    
    @OneToMany(mappedBy = "courseQnA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnAAnswer> answers;
    // 엔티티가 저장되기 전에 createdAt 값을 자동으로 설정
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 엔티티가 업데이트될 때 updatedAt 값을 자동으로 갱신
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}