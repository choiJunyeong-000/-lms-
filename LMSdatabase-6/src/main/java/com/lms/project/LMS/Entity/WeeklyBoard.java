package com.lms.project.LMS.Entity;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class WeeklyBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 해당 게시판이 속한 주차
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "week_id", nullable = false)
    private Week week;
    
    // 게시판 제목
    private String title;
    
    // 생성 시간
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "weeklyBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyPost> posts; // WeeklyPost 엔티티에서 매핑된 필드 이름으로 설정
}