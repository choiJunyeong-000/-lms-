package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Team;
import com.lms.project.LMS.Entity.TeamProject;
import com.lms.project.LMS.Repository.TeamProjectRepository;
import com.lms.project.LMS.Repository.TeamRepository;
import com.lms.project.LMS.Repository.CourseRepository;  // 추가
import com.lms.project.LMS.DTO.TeamProjectDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamProjectService {

    private final TeamProjectRepository teamProjectRepository;
    private final TeamRepository teamRepository;
    private final CourseRepository courseRepository;  // 추가

    public TeamProjectService(TeamProjectRepository teamProjectRepository,
                              TeamRepository teamRepository,
                              CourseRepository courseRepository) {  // 추가
        this.teamProjectRepository = teamProjectRepository;
        this.teamRepository = teamRepository;
        this.courseRepository = courseRepository;  // 추가
    }

    // 모든 팀 프로젝트 조회
    public List<TeamProject> getAllTeamProjects() {
        return teamProjectRepository.findAll();
    }

    // 특정 팀의 프로젝트 조회
    public List<TeamProject> getProjectsByTeamId(Long teamId) {
        List<TeamProject> projects = teamProjectRepository.findByTeamId(teamId);
        return projects;
    }


    // 팀 프로젝트 생성
    public TeamProject createTeamProject(TeamProjectDto dto) {
        // (1) 팀 찾기
        Team team = teamRepository.findById(dto.getTeamId())
            .orElseThrow(() -> new IllegalArgumentException(
                    "해당 팀이 존재하지 않습니다. ID: " + dto.getTeamId()));

        // (2) 코스 찾기
        Course course = courseRepository.findById(dto.getCourseId())  // 추가
            .orElseThrow(() -> new IllegalArgumentException(
                    "해당 코스가 존재하지 않습니다. ID: " + dto.getCourseId()));

        // (3) TeamProject 생성 & 값 설정
        TeamProject teamProject = new TeamProject();
        teamProject.setTeam(team);
        teamProject.setCourse(course);  // 추가
        teamProject.setProjectName(dto.getProjectName());
        teamProject.setDeadline(dto.getDeadline());

        return teamProjectRepository.save(teamProject);
    }
}
