package com.lms.project.LMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.lms.project.LMS.Entity.AssignmentSubmission;
import com.lms.project.LMS.DTO.AssignmentSubmissionDTO;
import com.lms.project.LMS.Service.AssignmentSubmissionService;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentSubmissionController {

    @Autowired
    private AssignmentSubmissionService assignmentSubmissionService;

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(@RequestHeader("studentid") String studentId,
                                              @PathVariable Long assignmentId,
                                              @RequestParam("file") MultipartFile file) {
        try {
            AssignmentSubmission submission = assignmentSubmissionService.submitAssignment(assignmentId, studentId, file);
            return ResponseEntity.ok("과제 제출 완료! 제출 파일: " + submission.getFileUrl());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("과제 제출 실패: " + e.getMessage());
        }
    }

    // 특정 과제에 대한 제출 내역을 DTO 리스트로 조회하는 API
    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        try {
            List<AssignmentSubmissionDTO> dtoList = assignmentSubmissionService.getSubmissionDTOsByAssignmentId(assignmentId);
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("제출 내역 조회 실패: " + e.getMessage());
        }
    }
}
