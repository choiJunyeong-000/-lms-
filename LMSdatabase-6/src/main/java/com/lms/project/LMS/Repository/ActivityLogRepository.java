package com.lms.project.LMS.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.project.LMS.Entity.ActivityLog;

/**
 * 활동 로그 데이터를 관리하는 리포지토리
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	// 특정 멤버의 활동 로그 조회
	List<ActivityLog> findByMemberId(Long memberId);

	// 특정 활동(Action)에 대한 로그 조회
	List<ActivityLog> findByAction(String action);

	// 특정 시간 범위의 활동 로그 조회
	List<ActivityLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
 