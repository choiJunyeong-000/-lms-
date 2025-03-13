package com.lms.project.LMS.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 시험 고유 ID

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course; // 시험이 속한 강의

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 시험을 만든 교수 추가

    private String title; // 시험 제목
    private String examDate; // 시험 날짜 및 시간
    private Double totalPoints; // 시험 총 점수

    @Lob
    private String description; // 시험 설명

    private String examType; // 시험 유형 (예: 중간고사, 기말고사)

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 이쪽이 주체인 관계
    private List<ExamQuestion> questions = new ArrayList<>(); // 시험 문제 목록
    
    

    public void addQuestion(ExamQuestion question) {
        question.setExam(this);
        questions.add(question);
    }
}
