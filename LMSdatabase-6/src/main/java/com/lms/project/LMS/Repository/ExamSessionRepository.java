package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.project.LMS.Entity.ExamSession;

/**
 * 시험 세션(ExamSession) 데이터를 관리하는 리포지토리 - 기본적인 CRUD 기능 제공 (JpaRepository 자동 포함)
 */
@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
}
                                                                                   