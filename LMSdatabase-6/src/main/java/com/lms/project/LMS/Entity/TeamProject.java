package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TeamProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 프로젝트 이름
    @Column(name = "project_name")
    private String projectName;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    // 마감일
    private LocalDateTime deadline;

    // 팀과의 관계 (ManyToOne 또는 OneToOne 중 원하는 방식 사용)
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false) // team_id가 null이면 에러
    private Team team;
    
    // 예: status 필드가 있다면 추가
    // private String status;
}
