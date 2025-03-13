package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 과제 정보를 관리하는 테이블 - 강의와 연계된 과제의 세부 정보를 저장 - 제목, 설명, 마감일, 배점 등을 관리
 */
@Entity
@Getter
@Setter
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 과제 고유 ID

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // 강의 정보 (필수)

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "week_id", nullable = false)
    private Week week; // 특정 주차 정보 (필수)

    @Column(nullable = false)
    private String title; // 과제 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 과제 설명

    @Column(nullable = false)
    private LocalDateTime dueDate; // 과제 마감일 (LocalDateTime 사용)

    @Column(nullable = false)
    private LocalDateTime startDate; // 과제 제출 가능 시작일 (LocalDateTime 사용)

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 과제 수정 시간 (자동 설정)

    @Column(nullable = false)
    private boolean isActive = true; // 과제 활성 상태 (기본값: 활성)

    @Column(nullable = false)
    private int points; // 과제 배점 (예: 100점 만점)

  /*  // 파일 제출 관련 추가
   @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssignmentSubmission> submissions; // 과제 제출 정보 */

    // 기본 생성자 (JPA에서 필요)
    protected Assignment() {}

    // 필수 필드를 포함한 생성자
    public Assignment(Course course, Week week, String title, String description, LocalDateTime dueDate, LocalDateTime startDate, int points) {
        this.course = course;
        this.week = week;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.points = points;
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // 수정된 시간 자동 업데이트
    @PrePersist
    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", course=" + course.getName() +
                ", week=" + week.getWeekNumber() + "주차" +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", startDate=" + startDate +
                ", points=" + points +
                ", isActive=" + isActive +
                '}';
    }
}