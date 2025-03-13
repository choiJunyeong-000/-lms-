package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 강의 피드백 데이터를 관리하는 리포지토리 - 특정 강의의 피드백 조회 - 특정 학생(Member)의 피드백 조회 - 특정
 * 상태("APPROVED", "PENDING")의 피드백 조회 - 특정 평점 이상의 피드백 조회 - 특정 강의에서 특정 학생이 작성한 피드백
 * 조회
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	// ✅ 특정 강의의 피드백 조회
	List<Feedback> findByCourseId(Long courseId);

	// ✅ 특정 학생(Member)의 피드백 조회
	List<Feedback> findByMemberId(Long memberId);

	// ✅ 특정 상태("APPROVED", "PENDING")의 피드백 조회
	List<Feedback> findByStatus(String status);

	// ✅ 특정 평점 이상의 피드백 조회
	List<Feedback> findByRatingGreaterThanEqual(int rating);

	// ✅ 특정 강의에서 특정 학생(Member)이 작성한 피드백 조회
	List<Feedback> findByCourseIdAndMemberId(Long courseId, Long memberId);
}
