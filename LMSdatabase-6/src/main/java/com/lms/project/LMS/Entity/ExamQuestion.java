package com.lms.project.LMS.Entity;

import java.util.List;

import com.lms.project.LMS.Enum.ExamQuestionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * 시험 문제 정보를 관리하는 엔티티
 * 
 * - 시험과 연계된 각 문제의 세부 정보를 저장합니다.
 */
@Entity
@Getter
@Setter
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private int customId; // 시험별 문제 ID
    private String questionText; // 문제 텍스트

    @Enumerated(EnumType.STRING)
    private ExamQuestionType type; // 문제 유형 (객관식, 서술형)

    private int score; // 문제 점수

    @ElementCollection
    private List<String> answers; // 선택지 목록

    private String correctAnswer; // 정답

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonBackReference
    private Exam exam; // 시험과의 연관 관계

    @OneToOne(mappedBy = "examQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private EssayExamQuestion essayExamQuestion; // 서술형 문제에 필요한 추가 정보와의 관계

    // 추가된 생성자
    public ExamQuestion(Long id) {
        this.id = id;
    }

    // 기본 생성자
    public ExamQuestion() {
    }
}
