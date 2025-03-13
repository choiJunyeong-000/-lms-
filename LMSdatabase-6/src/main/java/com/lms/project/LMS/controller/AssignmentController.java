package com.lms.project.LMS.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.project.LMS.DTO.AssignmentDTO;
import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Service.AssignmentService;
import com.lms.project.LMS.Service.CourseService;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Service.WeekService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000") // CORS 허용
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private WeekService weekService;


    // 📌 주차별 과제 추가
    @PostMapping("/{courseId}/weeks/{weekNumber}/assignments")
    public ResponseEntity<?> addAssignment(@RequestHeader Long memberId,
                                           @PathVariable Long courseId,
                                           @PathVariable int weekNumber,
                                           @RequestBody Assignment assignment) {
        Optional<Member> memberOptional = memberService.findById(memberId);
        if (!memberOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
        }

        Member member = memberOptional.get();
        // 교수 또는 관리자 권한 확인
        if (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        // 강좌 확인
        Optional<Course> courseOptional = courseService.findCourseById(courseId);
        if (!courseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의를 찾을 수 없습니다.");
        }

        Course course = courseOptional.get();

        // 주차 정보 조회 및 설정
        Optional<Week> weekOptional = weekService.findByCourseIdAndWeekNumber(course.getId(), weekNumber);
        if (!weekOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주차 정보를 찾을 수 없습니다.");
        }

        Week week = weekOptional.get();
        assignment.setCourse(course);
        assignment.setWeek(week);
        assignment.setStartDate(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        try {
            Assignment savedAssignment = assignmentService.saveAssignment(assignment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAssignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("과제 저장 중 오류가 발생했습니다.");
        }
    }
    @GetMapping("/{courseId}/weeks/{weekNumber}/assignments")
    public ResponseEntity<?> getAssignments(@PathVariable Long courseId, @PathVariable int weekNumber) {
        try {
            List<AssignmentDTO> assignments = assignmentService.getAssignments(courseId, weekNumber);
            if (assignments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("해당 주차의 과제가 없습니다.");
            }
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("과제 조회 중 오류가 발생했습니다.");
        }
    }
    // 📌 특정 주차의 과제 조회 API
 // 📌 특정 과제 세부 정보 조회 API
    @GetMapping("/{courseId}/weeks/{weekNumber}/assignments/{assignmentId}")
    public ResponseEntity<?> getAssignmentDetail(@PathVariable Long courseId, 
                                                 @PathVariable int weekNumber, 
                                                 @PathVariable Long assignmentId) {
        try {
            // 과제를 ID로 조회
            Optional<Assignment> assignmentOptional = assignmentService.findById(assignmentId);
            if (!assignmentOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("과제를 찾을 수 없습니다.");
            }

            Assignment assignment = assignmentOptional.get();
            
            // 과제가 해당 강좌와 주차에 맞는지 확인
            if (!assignment.getCourse().getId().equals(courseId) || 
                assignment.getWeek().getWeekNumber() != weekNumber) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 과제 정보입니다.");
            }

            // 과제 세부 정보 반환
            return ResponseEntity.ok(new AssignmentDTO(assignment)); // AssignmentDTO로 변환하여 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("과제 세부 정보를 불러오는 데 실패했습니다.");
        }
    }
 
}
