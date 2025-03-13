package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.ActivityLog;
import com.lms.project.LMS.Repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 활동 로그 관리 서비스 클래스
 */
@Service
public class ActivityLogService {

	private final ActivityLogRepository activityLogRepository;

	public ActivityLogService(ActivityLogRepository activityLogRepository) {
		this.activityLogRepository = activityLogRepository;
	}

	// 모든 활동 로그 조회
	public List<ActivityLog> getAllLogs() {
		return activityLogRepository.findAll();
	}

	// 특정 멤버의 활동 로그 조회
	public List<ActivityLog> getLogsByMemberId(Long memberId) {
		return activityLogRepository.findByMemberId(memberId);
	}

	// 특정 활동(Action)에 대한 로그 조회
	public List<ActivityLog> getLogsByAction(String action) {
		return activityLogRepository.findByAction(action);
	}

	// 특정 시간 범위의 활동 로그 조회
	public List<ActivityLog> getLogsByTimestamp(LocalDateTime start, LocalDateTime end) {
		return activityLogRepository.findByTimestampBetween(start, end);
	}

	// 활동 로그 저장
	public ActivityLog saveLog(ActivityLog log) {
		return activityLogRepository.save(log);
	}
}
