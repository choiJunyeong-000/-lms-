package com.lms.project.LMS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.DTO.WeeklyBoardRequest;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.WeeklyBoard;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Service.WeeklyBoardService;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

/**
 * 주차별 게시판 컨트롤러
 */
@RestController
@RequestMapping("/weekly-board")
@CrossOrigin(origins = "http://localhost:3000")  // CORS 설정
public class WeeklyBoardController {
    private final WeeklyBoardService boardService;
    private final MemberService memberService;

    public WeeklyBoardController(WeeklyBoardService boardService, MemberService memberService) {
        this.boardService = boardService;
        this.memberService = memberService;
    }

    /**
     * 게시판 생성 (교수 또는 관리자만 가능)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@RequestBody WeeklyBoardRequest request) {
        try {
            // memberId를 기반으로 회원 조회
            Optional<Member> memberOptional = memberService.findById(request.getMemberId());
            if (!memberOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
            }
            Member member = memberOptional.get();

            // 교수(PROFESSOR) 또는 관리자(ADMIN)만 게시판 생성 가능
            if (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            WeeklyBoard board = boardService.createBoard(request);
            return ResponseEntity.ok(board);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }



    /**
     * 특정 강의(courseId)와 주차(weekNumber)의 게시판 목록 조회 
     */
    @GetMapping("/course/{courseId}/week/{weekNumber}/boards")
    public ResponseEntity<List<WeeklyBoardRequest>> getBoardsByCourseAndWeek(
        @PathVariable Long courseId,
        @PathVariable int weekNumber
    ) {
        try {
            List<WeeklyBoard> boards = boardService.getBoardsByCourseAndWeek(courseId, weekNumber);

            List<WeeklyBoardRequest> response = boards.stream()
                .map(board -> new WeeklyBoardRequest(
                    board.getId(),
                    board.getTitle(), 
                    board.getWeek().getWeekNumber(), 
                    board.getCourse().getId(), 
                    null
                ))
                .toList();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
