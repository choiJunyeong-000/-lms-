package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Team;
import com.lms.project.LMS.Entity.TeamMember;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Course; // ✅ Course 엔티티 추가
import com.lms.project.LMS.Repository.TeamMemberRepository;
import com.lms.project.LMS.Repository.TeamRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.CourseRepository; // ✅ CourseRepository 추가
import com.lms.project.LMS.DTO.TeamDTO;
import com.lms.project.LMS.Enum.Role;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository; // ✅ CourseRepository 추가

    public TeamService(TeamRepository teamRepository, 
                       TeamMemberRepository teamMemberRepository, 
                       MemberRepository memberRepository,
                       CourseRepository courseRepository) { // ✅ CourseRepository 주입
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository; // ✅ 추가
    }

    // ✅ 팀 목록 조회
    public List<TeamDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream().map(TeamDTO::new).collect(Collectors.toList());
    }
 // TeamService.java에 추가
    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("❌ 해당 팀이 존재하지 않습니다. ID: " + id));

        return new TeamDTO(team); // ✅ TeamDTO 생성자를 호출하여 반환
    }


    // ✅ 특정 팀 조회
 // TeamService.java 내에 추가
    public List<TeamDTO> getTeamsByCourseId(Long courseId) {
        List<Team> teams = teamRepository.findByCourseId(courseId);
        return teams.stream()
                    .map(TeamDTO::new)
                    .collect(Collectors.toList());
    }


    // ✅ 특정 팀의 팀원 목록 조회
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    // ✅ 전체 팀원 목록 조회
    public List<TeamMember> getAllTeamMembers() {
        return teamMemberRepository.findAll();
    }

    // ✅ 팀 생성
    public Team createTeam(TeamDTO teamDto) {
        Team team = new Team();
        team.setName(teamDto.getName());
        team.setDescription(teamDto.getDescription());
        team.setDeadline(teamDto.getDeadline());

        // 🔥 courseId가 주어진 경우 Course 엔티티를 조회 후 설정
        if (teamDto.getCourseId() != null) {
            Course course = courseRepository.findById(teamDto.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("❌ 해당 ID의 강좌가 존재하지 않습니다: " + teamDto.getCourseId()));
            team.setCourse(course); // ✅ Course 설정 추가
        } else {
            throw new IllegalArgumentException("❌ Course ID가 필요합니다.");
        }

        return teamRepository.save(team);
    }

    // ✅ 팀원 추가 기능
    public TeamMember addTeamMember(Long teamId, Long memberId, String roleStr) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("❌ 팀이 존재하지 않습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("❌ 멤버가 존재하지 않습니다."));

        // 🔥 String → Role Enum 변환
        Role role;
        try {
            role = Role.valueOf(roleStr); // "팀원" -> Role.팀원
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("❌ 올바르지 않은 역할 값입니다: " + roleStr);
        }

        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setMember(member);
        teamMember.setRole(role);

        return teamMemberRepository.save(teamMember);
    }
}
