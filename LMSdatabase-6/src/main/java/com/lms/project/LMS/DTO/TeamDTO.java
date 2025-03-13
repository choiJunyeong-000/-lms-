package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Team;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private List<TeamMemberDTO> teamMembers;
    private Long courseId;

    // ✅ 기본 생성자 추가 (Jackson이 필요로 함)
    public TeamDTO() {}

    // 기존 생성자 유지
    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.deadline = team.getDeadline() != null ? team.getDeadline() : LocalDateTime.now();
        this.courseId = team.getCourse().getId();

        if (team.getTeamMembers() != null) {
            this.teamMembers = team.getTeamMembers().stream()
                    .map(TeamMemberDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.teamMembers = null;
        }
    }
}
