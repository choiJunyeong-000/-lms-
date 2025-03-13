package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.WeeklyBoard;

import java.util.List;
import java.util.Optional;

/**
 * 주차별 게시판 레포지토리
 */
public interface WeeklyBoardRepository extends JpaRepository<WeeklyBoard, Long> {
    List<WeeklyBoard> findByWeekId(Long weekId);
    
    List<WeeklyBoard> findByWeek_Course_IdAndWeek_WeekNumber(Long courseId, int weekNumber);
    
    List<WeeklyBoard> findAllByWeekId(Long weekId);
}