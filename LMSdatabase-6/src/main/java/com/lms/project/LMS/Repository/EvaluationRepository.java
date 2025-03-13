package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.project.LMS.Entity.Evaluation;

/**
 * 평가(Evaluation) 데이터를 관리하는 리포지토리 - 기본적인 CRUD 기능 제공 (JpaRepository 자동 포함)
 */
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
