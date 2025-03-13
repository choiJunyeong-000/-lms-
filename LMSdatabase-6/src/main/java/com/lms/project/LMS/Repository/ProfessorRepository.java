package com.lms.project.LMS.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.project.LMS.Entity.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
	Optional<Professor> findByMemberId(Long memberId);
	
}
