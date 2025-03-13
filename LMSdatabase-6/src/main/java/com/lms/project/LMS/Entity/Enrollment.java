package com.lms.project.LMS.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 강의 등록 고유 ID

    // ✅ 학생 대신 Member와 관계 설정 (LAZY 로딩 적용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore  // 🔥 JSON 직렬화에서 제외
    private Member member; 

    // ✅ 강의(Course)와 관계 설정 (LAZY 로딩 적용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore  // 🔥 JSON 직렬화에서 제외
    private Course course; 

    @Enumerated(EnumType.STRING)
    private Status status; // 교수의 승인 상태 (대기, 승인됨, 거절됨)

    @Column(name = "student_id")
    private String studentId;

    public enum Status {
        PENDING, APPROVED, REJECTED // 수강 신청 상태
    }

    public Enrollment() {
        this.status = Status.PENDING;
    }

    public Enrollment(Member member, Course course) {
        this.member = member;
        this.course = course;
        this.status = Status.PENDING;
    }
}
