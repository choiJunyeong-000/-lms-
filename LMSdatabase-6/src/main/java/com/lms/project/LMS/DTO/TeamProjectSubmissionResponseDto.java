// TeamProjectSubmissionResponseDto.java
package com.lms.project.LMS.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TeamProjectSubmissionResponseDto {
    private Long id;               // 제출 고유 ID
    private Long teamProjectId;    // 해당 팀 프로젝트 ID
    private Long teamId; // 추가
    private String projectName;    // 팀 프로젝트 이름
    private LocalDateTime deadline;// 팀 프로젝트 마감일
    private String teamName;       // 팀 이름
    private String files;          // 제출한 파일명
    private LocalDateTime submittedAt; // 제출 시간
}
