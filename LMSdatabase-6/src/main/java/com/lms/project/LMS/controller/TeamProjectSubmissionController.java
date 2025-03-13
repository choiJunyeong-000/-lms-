package com.lms.project.LMS.controller;

import com.lms.project.LMS.Entity.TeamProjectSubmission;
import com.lms.project.LMS.DTO.TeamProjectSubmissionResponseDto;
import com.lms.project.LMS.Service.TeamProjectSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/team-project-submissions")
@CrossOrigin(origins = "http://localhost:3000")
public class TeamProjectSubmissionController {

    private final TeamProjectSubmissionService submissionService;

    public TeamProjectSubmissionController(TeamProjectSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // 팀 프로젝트 제출 API
    @PostMapping("/{teamProjectId}/{teamId}/submit")
    public ResponseEntity<?> submitTeamProject(@PathVariable Long teamProjectId,
                                               @PathVariable Long teamId,
                                               @RequestParam("file") MultipartFile file) {
        try {
            TeamProjectSubmission submission = submissionService.submitTeamProject(teamProjectId, teamId, file);
            return ResponseEntity.ok("✅ 팀 프로젝트 제출 완료! 파일명: " + submission.getFiles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("❌ 제출 실패: " + e.getMessage());
        }
    }

    // 제출 내역 조회 API (DTO 버전)
    @GetMapping
    public ResponseEntity<?> getSubmissionDtos(@RequestParam(required = false) Long teamId) {
        try {
            List<TeamProjectSubmissionResponseDto> dtos;
            if (teamId != null) {
                dtos = submissionService.getSubmissionDtosByTeamId(teamId);
            } else {
                dtos = submissionService.getAllSubmissionDtos();
            }

            if (dtos == null || dtos.isEmpty()) {
                return ResponseEntity.ok("⚠️ 제출된 프로젝트가 없습니다.");
            }

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("❌ 제출 내역 조회 실패: " + e.getMessage());
        }
    }
}
