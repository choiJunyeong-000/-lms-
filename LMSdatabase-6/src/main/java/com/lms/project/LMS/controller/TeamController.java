	package com.lms.project.LMS.controller;
	
	import com.lms.project.LMS.Entity.Team;
	import com.lms.project.LMS.Entity.TeamMember;
	import com.lms.project.LMS.Service.TeamService;
	import com.lms.project.LMS.DTO.TeamDTO;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.*;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.stream.Collectors;
	
	@RestController
	@RequestMapping("/api")
	public class TeamController {
	    private final TeamService teamService;
	
	    public TeamController(TeamService teamService) {
	        this.teamService = teamService;
	    }
	    
	    // 팀 생성 API: TeamService.createTeam는 TeamDto를 받으므로 변경합니다.
	    
	    @PostMapping("/teams")
	    public ResponseEntity<?> createTeam(@RequestBody TeamDTO teamDto) { // ✅ TeamDTO로 변경
	        System.out.println("✅ 팀 생성 요청 도착! 받은 데이터: " + teamDto);
	        Team savedTeam = teamService.createTeam(teamDto);
	        return ResponseEntity.ok(new TeamDTO(savedTeam));
	    }

	    
	    @PostMapping("/team-members")
	    public ResponseEntity<?> addTeamMember(@RequestBody Map<String, Object> request) {
	        Long teamId = Long.valueOf(request.get("teamId").toString());
	        Long memberId = Long.valueOf(request.get("memberId").toString());
	        String role = request.get("role").toString();
	
	        System.out.println("✅ 팀원 추가 요청 - 팀 ID: " + teamId + ", 멤버 ID: " + memberId + ", 역할: " + role);
	
	        TeamMember newMember = teamService.addTeamMember(teamId, memberId, role);
	        return ResponseEntity.ok(newMember);
	    }
	    
	 // ✅ 전체 팀 조회 API
	    @GetMapping("/team")
	    public List<TeamDTO> getTeams(@RequestParam(required = false) Long courseId) {
	        if (courseId != null) {
	            return teamService.getTeamsByCourseId(courseId);
	        }
	        return teamService.getAllTeams();
	    }

	    // ✅ 특정 팀 조회 API
	    @GetMapping("/teams/{id}")
	    public TeamDTO getTeamById(@PathVariable Long id) { // ✅ getTeamById() 오류 수정
	        return teamService.getTeamById(id);
	    }

	    // ✅ 특정 팀의 팀원 목록 조회 API
	    @GetMapping("/team-members")
	    public ResponseEntity<?> getTeamMembers(@RequestParam(required = false) Long teamId) {
	        List<TeamMember> members;
	        if (teamId != null) {
	            members = teamService.getTeamMembers(teamId);
	        } else {
	            members = teamService.getAllTeamMembers();
	        }

	        List<Map<String, Object>> result = members.stream().map(tm -> {
	            Map<String, Object> map = new HashMap<>();
	            map.put("id", tm.getId());
	            
	            if (tm.getMember() != null) { // ✅ Null 체크 추가
	                map.put("member_id", tm.getMember().getId());
	                map.put("name", tm.getMember().getName());
	            } else {
	                map.put("member_id", null);
	                map.put("name", "Unknown");
	            }

	            map.put("role", tm.getRole());
	            map.put("team_id", tm.getTeam().getId());
	            return map;
	        }).collect(Collectors.toList());

	        return ResponseEntity.ok(result);
	    }

	}
