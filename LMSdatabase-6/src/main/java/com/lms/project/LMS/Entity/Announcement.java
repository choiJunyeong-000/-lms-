package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 공지사항 정보를 관리하는 엔티티
 * 
 * - 강의와 연계된 공지사항을 저장 - 관리자 또는 교수가 작성하여 학생들에게 전달 - 제목, 내용, 작성자, 활성 상태 등의 정보를 포함
 */
@Entity
@Getter
@Setter
public class Announcement {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id; // 공지사항 ID

	    @ManyToOne
	    @JoinColumn(name = "course_id", nullable = false)
	    private Course course; // 강의 연관 관계

	    private String title; // 제목

	    @Column(columnDefinition = "TEXT")
	    private String content; // 내용

	    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	    @JoinColumn(name = "week_id")
	    private Week week; // 해당 주차 정보

	    @ManyToOne
	    @JoinColumn(name = "member_id", nullable = false)
	    private Member createdBy; // 작성자 정보 (createdBy → member로 변경)

	    private boolean isActive = true; // 활성 상태
	    
	    
	    private LocalDateTime createdAt; // 생성 날짜
	    
	    @PrePersist
	    protected void onCreate() {
	        this.createdAt = LocalDateTime.now();
	    }

	   
}
