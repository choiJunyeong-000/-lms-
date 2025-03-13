package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Getter
@Setter
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 설문 ID

    @Column(nullable = false)
    private String title; // 설문 제목

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyType surveyType; // 설문 유형 (강의평가, 팀원평가 등)
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    @JsonIgnoreProperties({ "assignments", "announcements", "contents", "virtualLectures", "feedbacks", "weeks", "enrollments" })
    private Course course; 

    

    @Column(columnDefinition = "TEXT")
    private String description; // 설문 설명

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @PrePersist
    public void prePersist() {
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
        if (this.endDate == null) {
            this.endDate = LocalDateTime.now().plusWeeks(2);
        }
    }



    @Column(nullable = false)
    private boolean isActive = true; // 활성화 여부

    @Column(columnDefinition = "TEXT")
    private String options; // 선택지를 JSON 또는 쉼표로 구분된 문자열 형태로 저장 가능
}
