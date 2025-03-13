package com.lms.project.LMS.Entity;

import java.time.LocalDate;
import java.util.List;

import com.lms.project.LMS.Enum.CourseStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 강의 고유 ID

    private String name; // 강의 이름
    private String description; // 강의 설명
    private LocalDate startDate; // 강의 시작일
    private LocalDate endDate; // 강의 종료일
    @Enumerated(EnumType.STRING) // 강의 상태를 Enum으로 관리 (예: 진행 중, 종료 등)
    private CourseStatus status; // 강의 상태

    @Column(nullable = false)
    private int credits; // 강의 학점

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 강의를 담당하는 교수 (Member 테이블과 연결)

    // 기존의 교수 정보를 설정하는 메소드 추가
    public void setProfessor(Member professor) {
        this.member = professor; // member를 professor로 설정
    }
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQnA> courseQnAs; // 강좌에 속한 Q&A 목록
    
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exam> exams; // Add this relationship with cascade
    
    // 교수의 학번을 저장하는 필드 추가
    private String professorStudentId;

    // 교수의 학번을 설정하는 메서드
    public void setProfessorStudentId(String professorStudentId) {
        this.professorStudentId = professorStudentId;
    }

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments; // 강의에 등록된 학생 목록

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments; // 강의에 연계된 과제 목록

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements; // 강의 공지사항 목록

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents; // 강의 콘텐츠 목록

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VirtualLecture> virtualLectures; // 강의와 연계된 가상 강의 목록

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks; // 강의 피드백 목록

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Week> weeks; // 강의 주차 목록
    
    // 새로운 syllabusFilePath 필드 추가
    @Column(name = "syllabus_file_path")
    private String syllabusFilePath; // 강의 계획서 파일 경로

    // syllabusFilePath 설정 메소드 추가
    public void setSyllabusFilePath(String syllabusFilePath) {
        this.syllabusFilePath = syllabusFilePath;
    }

    // 새로 추가된 courseType 필드
    @Column(name = "course_type")
    private String courseType; // 수업 종류 (예: 전필, 전선, 교양 등)

    // courseType 설정 메소드 추가
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    // 교수의 username을 가져오는 메서드 추가
    public String getProfessorName() {
        return member != null ? member.getUsername() : null;
    }
 // 교수의 학번을 가져오는 메서드 추가
    public String getProfessorStudentId() {
        return member != null ? member.getStudentId() : null;
    }
 // 교수의 ID를 가져오는 메서드 추가
    public Long getProfessorId() {
        return member != null ? member.getId() : null;
    }
    
    
}
