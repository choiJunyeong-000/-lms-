package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 통계 데이터를 관리하는 리포지토리
 */
@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

	// 특정 강의의 통계 데이터 조회
	List<Statistics> findByCourseId(Long courseId);

	// 특정 통계 항목 이름으로 조회
	List<Statistics> findByMetricName(String metricName);
}
