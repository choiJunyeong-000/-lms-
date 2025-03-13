// AssignmentSubmissionDTO.java
package com.lms.project.LMS.DTO;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentSubmissionDTO {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String fileUrl;
    private LocalDateTime submittedAt;
}
