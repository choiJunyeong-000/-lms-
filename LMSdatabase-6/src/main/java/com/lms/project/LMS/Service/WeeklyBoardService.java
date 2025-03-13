package com.lms.project.LMS.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.DTO.WeeklyBoardRequest;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Entity.WeeklyBoard;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.WeekRepository;
import com.lms.project.LMS.Repository.WeeklyBoardRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * 주차별 게시판 서비스
 */
@Service
public class WeeklyBoardService {
    private final WeeklyBoardRepository boardRepository;
    private final WeekRepository weekRepository;
    private final CourseRepository courseRepository;

    public WeeklyBoardService(WeeklyBoardRepository boardRepository, WeekRepository weekRepository, CourseRepository courseRepository) {
        this.boardRepository = boardRepository;
        this.weekRepository = weekRepository;
        this.courseRepository = courseRepository;
    }

    // 주차별 게시판 생성 (Week 설정 추가)
    @Transactional
    public WeeklyBoard createBoard(WeeklyBoardRequest requestDTO) {
        if (requestDTO.getWeekNumber() <= 0) {
            throw new IllegalArgumentException("Week number must be greater than zero");
        }

        if (requestDTO.getCourseId() == null || requestDTO.getCourseId() <= 0) {
            throw new IllegalArgumentException("Invalid course ID");
        }

        if (requestDTO.getTitle() == null || requestDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty");
        }

        // 정확한 주차(Week) 찾기 → courseId와 weekNumber로 조회
        Week week = weekRepository.findByCourseIdAndWeekNumber(requestDTO.getCourseId(), requestDTO.getWeekNumber())
            .orElseThrow(() -> new EntityNotFoundException("Week not found for course ID: " 
                + requestDTO.getCourseId() + " and week number: " + requestDTO.getWeekNumber()));

        // 정확한 강의 찾기
        Course course = courseRepository.findById(requestDTO.getCourseId())
            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + requestDTO.getCourseId()));

        WeeklyBoard board = new WeeklyBoard();
        board.setWeek(week);
        board.setTitle(requestDTO.getTitle());
        board.setCourse(course);
        
        return boardRepository.save(board);
    }


    // 특정 주차의 게시판 조회
    public List<WeeklyBoard> getBoardsByWeek(Long weekId) {
        return boardRepository.findByWeekId(weekId);
    }

    // 주차 번호와 강좌 ID로 게시판 조회
    public List<WeeklyBoard> getBoardsByCourseAndWeek(Long courseId, int weekNumber) {
        return boardRepository.findByWeek_Course_IdAndWeek_WeekNumber(courseId, weekNumber);
    }
    public Optional<WeeklyBoard> findById(Long boardId) {
        return boardRepository.findById(boardId);
    }

}