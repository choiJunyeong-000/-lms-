package com.lms.project.LMS.Service;

import com.lms.project.LMS.DTO.AssignmentSubmissionDTO;
import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Entity.AssignmentSubmission;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.AssignmentRepository;
import com.lms.project.LMS.Repository.AssignmentSubmissionRepository;
import com.lms.project.LMS.Repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final MemberRepository memberRepository;
    private final Path uploadDir;

    @Autowired
    public AssignmentSubmissionService(
        AssignmentSubmissionRepository submissionRepository,
        AssignmentRepository assignmentRepository,
        MemberRepository memberRepository,
        @Value("${file.upload-dir}") String uploadDirPath) {  // 경로를 설정에서 주입
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.memberRepository = memberRepository;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
    }

    public AssignmentSubmission submitAssignment(Long assignmentId, String studentId, MultipartFile file) throws Exception {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        if (!assignmentOptional.isPresent()) {
            throw new Exception("과제를 찾을 수 없습니다.");
        }

        Optional<Member> studentOptional = memberRepository.findByStudentId(studentId);
        if (!studentOptional.isPresent()) {
            throw new Exception("학생 정보를 찾을 수 없습니다.");
        }

        Assignment assignment = assignmentOptional.get();
        Member student = studentOptional.get();

        // 📌 업로드 디렉토리 확인 및 생성
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 📌 파일 저장
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 📌 디버깅 로그 추가
        System.out.println("=== 저장된 파일 경로: " + filePath.toString());
        System.out.println("=== 파일 존재 여부: " + Files.exists(filePath));

        // 📌 제출 엔티티 저장
        AssignmentSubmission submission = new AssignmentSubmission(assignment, student, filePath.toString());
        return submissionRepository.save(submission);
    }


    // 특정 과제에 대한 모든 제출 내역을 DTO 리스트로 변환하여 반환하는 메서드
    public List<AssignmentSubmissionDTO> getSubmissionDTOsByAssignmentId(Long assignmentId) {
        List<AssignmentSubmission> submissions = submissionRepository.findByAssignment_Id(assignmentId);
        return submissions.stream().map(submission -> {
            AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
            dto.setId(submission.getId());
            dto.setAssignmentId(submission.getAssignment().getId());
            dto.setStudentId(submission.getStudent().getId());
            dto.setFileUrl(submission.getFileUrl());
            dto.setSubmittedAt(submission.getSubmittedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}
