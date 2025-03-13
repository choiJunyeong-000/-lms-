package com.lms.project.LMS.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TeamProjectDto {
    private Long teamProjectId;   // ✅ 추가된 필드 (프로젝트 ID)
    private Long teamId;          // JSON의 "team_id"
    private Long courseId;        // 추가
    private String projectName;   // JSON의 "project_name"
    private LocalDateTime deadline;
}
