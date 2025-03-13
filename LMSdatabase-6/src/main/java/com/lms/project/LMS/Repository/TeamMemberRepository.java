package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    // ✅ 특정 팀에 속한 팀원 목록 조회
    List<TeamMember> findByTeamId(Long teamId);
}
