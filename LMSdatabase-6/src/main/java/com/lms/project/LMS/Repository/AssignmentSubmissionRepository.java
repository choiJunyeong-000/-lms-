package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    // 특정 과제에 대한 모든 제출 조회
    List<AssignmentSubmission> findByAssignment_Id(Long assignmentId);

    // 특정 학생이 제출한 과제 조회
    List<AssignmentSubmission> findByStudent_Id(Long studentId);
}
