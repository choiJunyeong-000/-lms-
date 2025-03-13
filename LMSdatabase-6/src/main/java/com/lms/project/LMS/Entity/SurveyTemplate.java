package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 공통 설문 정보를 저장하는 테이블
 * 
 * - 모든 강의에서 공통으로 사용하는 설문 질문과 선택지를 관리합니다.
 * - 교수님이 추가로 질문을 등록할 수 있도록 별도의 Survey 엔티티와 연동될 수 있습니다.
 */
@Entity
@Getter
@Setter
public class SurveyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공통 설문 고유 ID

    @Column(nullable = false)
    private String question;  // 공통 설문 질문

    @Column(columnDefinition = "TEXT")
    private String options;   // 선택지 (JSON 형식으로 저장 가능)

    @Column(nullable = false)
    private boolean isActive = true; // 활성화 상태 (기본값: 활성화)
}
