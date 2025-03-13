package com.lms.project.LMS.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.project.LMS.DTO.WeekResponse;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.WeekRepository;

import jakarta.transaction.Transactional;

@Service
public class WeekService {

    private final CourseRepository courseRepository;
    private final WeekRepository weekRepository;

    @Autowired
    public WeekService(CourseRepository courseRepository, WeekRepository weekRepository) {
        this.courseRepository = courseRepository;
        this.weekRepository = weekRepository;
    }

    // Week 저장
    public Week save(Week week) {
        return weekRepository.save(week);
    }

    // Week 조회 (예시)
    public Week findById(Long id) {
        return weekRepository.findById(id).orElse(null);
    }

    // 주차가 이미 존재하는지 확인
    public boolean checkIfWeekExists(Long courseId, int weekNumber) {
        boolean exists = weekRepository.existsByCourseIdAndWeekNumber(courseId, weekNumber);
        System.out.println("✅ 주차 존재 여부 확인 - CourseID: " + courseId + ", Week: " + weekNumber + ", Exists: " + exists);
        return exists;
    }


    // 강의 주차 목록 조회
    public List<Week> getWeeksForCourse(Long courseId) {
        return weekRepository.findByCourseId(courseId); // WeekRepository에 정의된 메서드를 호출
    }
    
    @Transactional
    public Week addWeek(Long courseId, int weekNumber) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의를 찾을 수 없습니다."));

        // 중복 체크
        if (weekRepository.existsByCourseAndWeekNumber(course, weekNumber)) {
            throw new IllegalStateException("이미 존재하는 주차입니다.");
        }

        Week newWeek = Week.builder()
                .course(course)
                .weekNumber(weekNumber)
                .build();
        
        return weekRepository.save(newWeek);
    }
    
    public Optional<Week> findByCourseIdAndWeekNumber(Long courseId, int weekNumber) {
        return weekRepository.findByCourseIdAndWeekNumber(courseId, weekNumber);
    }
    
    public void saveAll(List<Week> weeks) {
        weekRepository.saveAll(weeks);
    }
    
    public List<Week> findByCourseId(Long courseId) {
        return weekRepository.findByCourseId(courseId);
    }
}