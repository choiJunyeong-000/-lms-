package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment; // 어떤 과제에 대한 제출인지

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Member student; // 제출한 학생 정보

    @Column(nullable = false)
    private String fileUrl; // 업로드된 파일의 저장 경로

    @Column(nullable = false)
    private LocalDateTime submittedAt; // 제출 시간

    // 기본 생성자
    protected AssignmentSubmission() {}

    public AssignmentSubmission(Assignment assignment, Member student, String fileUrl) {
        this.assignment = assignment;
        this.student = student;
        this.fileUrl = fileUrl;
        this.submittedAt = LocalDateTime.now();
    }
}
