package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.TeamMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamMemberDTO {
    private Long id;
    private String role;
    private Long memberId; 
    private String memberName;

    public TeamMemberDTO(TeamMember teamMember) {
        this.id = teamMember.getId();
        this.role = (teamMember.getRole() != null) ? teamMember.getRole().name() : "Unknown"; // ✅ 수정
        this.memberId = (teamMember.getMember() != null) ? teamMember.getMember().getId() : null;
        this.memberName = (teamMember.getMember() != null) ? teamMember.getMember().getName() : "Unknown";
    }

 }

