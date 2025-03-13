package com.lms.project.LMS.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

/**
 * 서술형 시험 문제와 관련된 정보를 저장하는 엔티티 - ExamQuestion과 연계하여 서술형 문제 데이터를 관리합니다. - 학생 답변 및
 * 교수의 점수를 저장합니다.
 */
@Entity
@Data
public class EssayExamQuestion {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id; // 고유 ID

   @OneToOne
   @JoinColumn(name = "question_id", nullable = false) // ExamQuestion 테이블의 ID를 참조
   private ExamQuestion examQuestion; // 서술형 문제와 연관된 ExamQuestion

   private String studentAnswer; // 학생의 답변
   private Integer score; // 교수님이 입력한 점수
   private String questionText; // 문제 텍스트

   @ManyToOne // 다대일 관계 설정
   @JoinColumn(name = "exam_id", nullable = false) // 외래 키 설정
   @JsonIgnore
   private Exam exam; // 해당 문제와 연결된 시험
}
