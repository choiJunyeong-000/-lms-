package com.lms.project.LMS.DTO;

import java.time.LocalDateTime;

import com.lms.project.LMS.Entity.Assignment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDTO {
	 private Long id;          // 과제 ID 추가
	 private String title;  // 과제 제목
	    private String description;  // 과제 설명
	    private LocalDateTime  dueDate;  // 제출 기한
	    
	    public AssignmentDTO(Assignment assignment) {
	        this.id = assignment.getId();
	        this.title = assignment.getTitle();
	        this.description = assignment.getDescription();
	        this.dueDate = assignment.getDueDate();
	    }

}