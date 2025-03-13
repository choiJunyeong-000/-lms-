package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender; // 보낸 사람 (studentId 사용)

    @Column(nullable = false)
    private String recipient; // 받는 사람 (studentId 사용)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 메시지 내용

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // 메시지 전송 시간

    @Column(nullable = false)
    private boolean isRead = false; // 읽음 여부 (기본값 false)

    // ✅ 읽음 상태 변경 메서드 추가
    public void markAsRead() {
        this.isRead = true;
    }
}
