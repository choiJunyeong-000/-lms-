package com.lms.project.LMS.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.project.LMS.Entity.VirtualLecture;

/**
 * 가상 강의 데이터를 관리하는 리포지토리
 */
@Repository
public interface VirtualLectureRepository extends JpaRepository<VirtualLecture, Long> {

	// ✅ 특정 강의에 연계된 가상 강의 조회 (공통)
	List<VirtualLecture> findByCourseId(Long courseId);

	// ✅ 특정 날짜 이후의 가상 강의 조회 (내 코드 유지)
	List<VirtualLecture> findByScheduledAtAfter(LocalDateTime scheduledAt);

	// ✅ 특정 일정의 가상 강의 조회 (팀원 코드 유지)
	List<VirtualLecture> findByScheduledAt(LocalDateTime scheduledAt);

	// ✅ 특정 강의 ID와 일정으로 가상 강의 조회 (팀원 코드 유지)
	List<VirtualLecture> findByCourseIdAndScheduledAt(Long courseId, LocalDateTime scheduledAt);

	// ✅ 제목에 특정 키워드를 포함하는 가상 강의 조회 (내 코드 유지)
	List<VirtualLecture> findByTitleContaining(String title);

	// ✅ 특정 교수가 만든 가상 강의 조회 (내 코드 유지)
	List<VirtualLecture> findByMemberId(Long memberId);
}
