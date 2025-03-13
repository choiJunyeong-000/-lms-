package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.project.LMS.Entity.TeamProject;
import java.util.List;

public interface TeamProjectRepository extends JpaRepository<TeamProject, Long> {
    // 특정 팀의 프로젝트 목록 조회 (여러 개의 프로젝트가 있을 수 있으므로 List<TeamProject>로 반환)
    List<TeamProject> findByTeamId(Long teamId);
}
