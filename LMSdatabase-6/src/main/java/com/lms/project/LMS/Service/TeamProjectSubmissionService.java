package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.TeamProjectSubmission;
import com.lms.project.LMS.Entity.TeamProject;
import com.lms.project.LMS.DTO.TeamProjectSubmissionResponseDto;
import com.lms.project.LMS.Repository.TeamProjectRepository;
import com.lms.project.LMS.Repository.TeamRepository;
import com.lms.project.LMS.Repository.TeamProjectSubmissionRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamProjectSubmissionService {

    private final TeamProjectSubmissionRepository submissionRepository;
    private final TeamProjectRepository teamProjectRepository;
    private final TeamRepository teamRepository;
    private final Path uploadDir;

    public TeamProjectSubmissionService(TeamProjectSubmissionRepository submissionRepository,
                                        TeamProjectRepository teamProjectRepository,
                                        TeamRepository teamRepository, @Value("${file.upload-dir}") String uploadDirPath) {
        this.submissionRepository = submissionRepository;
        this.teamProjectRepository = teamProjectRepository;
        this.teamRepository = teamRepository;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
    }

    public TeamProjectSubmission submitTeamProject(Long teamProjectId, Long teamId, MultipartFile file) throws Exception {
        // 1. 팀 프로젝트 존재 여부 확인
        Optional<TeamProject> projectOptional = teamProjectRepository.findById(teamProjectId);
        if (!projectOptional.isPresent()) {
            throw new Exception("팀 프로젝트를 찾을 수 없습니다.");
        }

        // 2. 팀 정보 확인
        Optional<com.lms.project.LMS.Entity.Team> teamOptional = teamRepository.findById(teamId);
        if (!teamOptional.isPresent()) {
            throw new Exception("팀 정보를 찾을 수 없습니다.");
        }

        TeamProject teamProject = projectOptional.get();
        com.lms.project.LMS.Entity.Team team = teamOptional.get();

        // 3. 파일 저장 처리
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);  // 업로드 폴더가 없으면 생성
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 디버깅용 로그 추가
        System.out.println("=== 저장된 파일 경로: " + filePath.toString());
        System.out.println("=== 파일 존재 여부: " + Files.exists(filePath));

        // 4. 제출 엔티티 생성 및 저장
        TeamProjectSubmission submission = new TeamProjectSubmission();
        submission.setTeamProject(teamProject);
        submission.setTeam(team);
        submission.setFiles(filePath.toString());  // 📌 절대 경로 저장
        submission.setSubmittedAt(java.time.LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    // 엔티티를 DTO로 변환하는 메소드
    private TeamProjectSubmissionResponseDto convertToDto(TeamProjectSubmission submission) {
        TeamProjectSubmissionResponseDto dto = new TeamProjectSubmissionResponseDto();
        dto.setId(submission.getId());
        dto.setTeamProjectId(submission.getTeamProject().getId());
        dto.setProjectName(submission.getTeamProject().getProjectName());
        dto.setDeadline(submission.getTeamProject().getDeadline());
        dto.setTeamName(submission.getTeam().getName());
        dto.setFiles(submission.getFiles());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setTeamId(submission.getTeam().getId());

        return dto;
    }

    // 팀 ID로 조회 후 DTO 리스트 반환
    public List<TeamProjectSubmissionResponseDto> getSubmissionDtosByTeamId(Long teamId) {
        List<TeamProjectSubmission> submissions = submissionRepository.findByTeamId(teamId);
        return submissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 전체 제출 내역 DTO 리스트 반환
    public List<TeamProjectSubmissionResponseDto> getAllSubmissionDtos() {
        List<TeamProjectSubmission> submissions = submissionRepository.findAll();
        return submissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
}
