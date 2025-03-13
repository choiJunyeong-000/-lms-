package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.TeamProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamProjectSubmissionRepository extends JpaRepository<TeamProjectSubmission, Long> {
    List<TeamProjectSubmission> findByTeamId(Long teamId);
}
