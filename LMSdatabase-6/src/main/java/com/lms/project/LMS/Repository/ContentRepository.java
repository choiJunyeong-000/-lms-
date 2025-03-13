package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Video;
import com.lms.project.LMS.Entity.Week;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 강의 콘텐츠 데이터를 관리하는 리포지토리 - 특정 강의에 속한 콘텐츠 조회 - 특정 주차(Week)별 콘텐츠 조회 - 제목 포함 검색 -
 * 업로드 날짜 범위 검색 - 활성 콘텐츠 조회
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

	// ✅ 특정 강의의 콘텐츠 조회
	List<Content> findByCourse(Course course);

	List<Content> findByCourseId(Long courseId); // courseId 기준 검색

	// ✅ 특정 제목을 포함하는 콘텐츠 검색
	List<Content> findByTitleContaining(String title);

	// ✅ 업로드된 시간 범위로 콘텐츠 검색
	List<Content> findByUploadedAtBetween(LocalDateTime start, LocalDateTime end);

	// ✅ 활성화된 콘텐츠 조회
	List<Content> findByIsActiveTrue();

	// ✅ 주차별 콘텐츠 조회 (팀원 코드 유지)
	List<Content> findByWeekId(Long weekId);

	List<Content> findByWeek(Week week);

	// ✅ 파일명으로 콘텐츠 검색
	Optional<Content> findByFileName(String fileName);
	
	List<Content> findAllByWeekId(Long weekId);
	
	Optional<Content> findByVideo(Video video);  // Video와 관련된 Content를 조회
	
	//전체 강의 개수 측정
		@Query("SELECT COUNT(c) FROM Content c WHERE c.course.id = :courseId")
	    long countByCourseId(Long courseId);
}
