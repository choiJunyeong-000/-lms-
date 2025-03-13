package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Announcement;
import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Entity.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공지사항 데이터를 관리하는 리포지토리
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

	// 특정 강의의 공지사항 조회
	List<Announcement> findByCourseId(Long courseId);

	// 특정 작성자에 의해 생성된 공지사항 조회
	List<Announcement> findByCreatedById(Long createdById);

	// 제목으로 공지사항 검색
	List<Announcement> findByTitleContaining(String title);
	
	  List<Announcement> findByCourseIdAndWeekWeekNumber(Long courseId, int weekNumber);
	  
	  List<Announcement> findByCourseOrderByCreatedAtDesc(Course course);
	  
	  List<Announcement> findAllByWeekId(Long weekId);
}
