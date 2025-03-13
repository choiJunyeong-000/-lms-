package com.lms.project.LMS.controller;

import com.lms.project.LMS.Entity.TeamProject;
import com.lms.project.LMS.Service.TeamProjectService;
import com.lms.project.LMS.DTO.TeamProjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/team-projects")
public class TeamProjectController {

    private final TeamProjectService teamProjectService;

    public TeamProjectController(TeamProjectService teamProjectService) {
        this.teamProjectService = teamProjectService;
    }

    // 특정 팀의 프로젝트 목록 조회 (teamProjectId 추가됨)
    @GetMapping
    public ResponseEntity<List<TeamProjectDto>> getTeamProjects(@RequestParam(required = false) Long teamId) {
        List<TeamProject> projects;
        if (teamId != null) {
            projects = teamProjectService.getProjectsByTeamId(teamId);
        } else {
            projects = teamProjectService.getAllTeamProjects();
        }

        List<TeamProjectDto> dtoList = projects.stream().map(project -> {
            
            
            TeamProjectDto dto = new TeamProjectDto();
            dto.setTeamProjectId(project.getId());  // ✅ teamProjectId 설정
            dto.setTeamId(project.getTeam().getId());
            dto.setCourseId(project.getCourse().getId());
            dto.setProjectName(project.getProjectName());
            dto.setDeadline(project.getDeadline());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }


    // 팀 프로젝트 생성 (기존 API 유지)
    @PostMapping
    public ResponseEntity<TeamProject> createTeamProject(@RequestBody TeamProjectDto dto) {
        TeamProject createdProject = teamProjectService.createTeamProject(dto);
        return ResponseEntity.ok(createdProject);
    }
}
