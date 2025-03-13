package com.lms.project.LMS.Service;

import com.lms.project.LMS.DTO.AssignmentDTO;
import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 과제 관리 서비스 클래스
 */
@Service
public class AssignmentService {

	private final AssignmentRepository assignmentRepository;

	public AssignmentService(AssignmentRepository assignmentRepository) {
		this.assignmentRepository = assignmentRepository;
	}

	// 모든 과제 조회
	public List<Assignment> getAllAssignments() {
		return assignmentRepository.findAll();
	}

	// 특정 강의의 과제 조회
	public List<Assignment> getAssignmentsByCourseId(Long courseId) {
		return assignmentRepository.findByCourseId(courseId);
	}

	// 마감일 이전의 과제 조회
	public List<Assignment> getAssignmentsDueBefore(LocalDateTime dueDate) {
		return assignmentRepository.findByDueDateBefore(dueDate);
	}

	// 활성화된 과제 조회
	public List<Assignment> getActiveAssignments() {
		return assignmentRepository.findByIsActiveTrue();
	}

	// 과제 저장
	public Assignment saveAssignment(Assignment assignment) {
		return assignmentRepository.save(assignment);
	}
	
	// 모든 과제 조회
		public List<AssignmentDTO> getAssignments(Long courseId, int weekNumber) {
	        List<Assignment> assignments = assignmentRepository.findByCourseIdAndWeek_WeekNumber(courseId, weekNumber);

	        // Assignment 엔티티에서 AssignmentDTO로 변환
	        return assignments.stream().map(assignment -> {
	            AssignmentDTO dto = new AssignmentDTO(assignment);
	            dto.setId(assignment.getId()); // ID 필드 추가
	            dto.setTitle(assignment.getTitle());
	            dto.setDescription(assignment.getDescription());
	            dto.setDueDate(assignment.getDueDate());
	            return dto;
	        }).collect(Collectors.toList());
	    }
		 public Optional<Assignment> findById(Long id) {
		        return assignmentRepository.findById(id); // JpaRepository에서 제공하는 findById 사용
		    }

}
