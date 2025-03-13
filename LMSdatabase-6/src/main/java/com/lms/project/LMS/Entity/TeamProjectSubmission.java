package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TeamProjectSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 팀 프로젝트에 제출한 것인지
    @ManyToOne
    @JoinColumn(name = "team_project_id", nullable = false)
    private TeamProject teamProject;

    // 제출한 팀 정보
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 업로드한 파일명들을 콤마(,)로 구분하여 저장 (실제 서비스에서는 별도 파일 관리 로직 구현 필요)
    private String files;

    // 제출 시간
    private LocalDateTime submittedAt = LocalDateTime.now();
}
