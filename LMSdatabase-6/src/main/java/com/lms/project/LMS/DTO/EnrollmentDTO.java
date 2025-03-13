package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Enrollment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentId;
    private Long courseId;      // 강의 아이디만 필요
    private String courseName;  // 강의 이름이 필요하다면 포함
    private Long memberId;      // 학생 아이디만 필요
    private String memberName;  // 학생 이름이 필요하다면 포함
    private String status;      // 상태 (대기, 승인됨, 거절됨)

    // 기본 생성자
    public EnrollmentDTO(Enrollment enrollment) {
        this.enrollmentId = enrollment.getId();
        this.courseId = enrollment.getCourse() != null ? enrollment.getCourse().getId() : null;
        this.courseName = enrollment.getCourse() != null ? enrollment.getCourse().getName() : null; // 이름이 필요하다면
        this.memberId = enrollment.getMember() != null ? enrollment.getMember().getId() : null;
        this.memberName = enrollment.getMember() != null ? enrollment.getMember().getName() : null; // 이름이 필요하다면
        this.status = enrollment.getStatus() != null ? enrollment.getStatus().name() : "Unknown"; // 상태 값
    }
}
