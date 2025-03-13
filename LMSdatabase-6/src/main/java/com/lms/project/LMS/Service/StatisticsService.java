package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Statistics;
import com.lms.project.LMS.Repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 통계 관리 서비스 클래스
 */
@Service
public class StatisticsService {

	private final StatisticsRepository statisticsRepository;

	public StatisticsService(StatisticsRepository statisticsRepository) {
		this.statisticsRepository = statisticsRepository;
	}

	// 모든 통계 데이터 조회
	public List<Statistics> getAllStatistics() {
		return statisticsRepository.findAll();
	}

	// 특정 강의의 통계 데이터 조회
	public List<Statistics> getStatisticsByCourseId(Long courseId) {
		return statisticsRepository.findByCourseId(courseId);
	}

	// 특정 통계 항목 이름으로 조회
	public List<Statistics> getStatisticsByMetricName(String metricName) {
		return statisticsRepository.findByMetricName(metricName);
	}

	// 통계 데이터 저장
	public Statistics saveStatistics(Statistics statistics) {
		return statisticsRepository.save(statistics);
	}
}
