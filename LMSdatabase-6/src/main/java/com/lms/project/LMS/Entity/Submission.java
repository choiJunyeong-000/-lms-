package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 과제 제출 정보를 관리하는 엔티티
 * 
 * - 학생이 제출한 답안 데이터를 저장합니다.
 */
@Entity
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 제출 고유 ID

    @ManyToOne(cascade = CascadeType.ALL)  // CascadeType.ALL을 추가
    @JoinColumn(name = "exam_id", nullable = false)
    @NotNull(message = "시험 정보가 반드시 포함되어야 합니다.")
    private Exam exam; // 시험 정보

   
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 제출한 학생

    @ElementCollection
    @MapKeyColumn(name = "question_id")
    @Column(name = "answer")
    private Map<Long, String> answers; // 객관식 답안

    @ElementCollection
    @MapKeyColumn(name = "question_id")
    @Column(name = "essay_answer")
    private Map<Long, String> essayAnswers; // 서술형 답안

    @ElementCollection
    @MapKeyColumn(name = "question_id")
    @Column(name = "essay_score")
    private Map<Long, Integer> essayScores; // 서술형 문제별 점수

    private int totalScore; // 총 점수 추가

    // 총 점수에 대한 getter와 setter 메서드
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalScore() {
        return totalScore;
    }
    
    
}
