package com.lms.project.LMS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.DTO.CourseResponse;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Service.CourseService;
import com.lms.project.LMS.Service.ExamService;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.SubmissionRepository;
import com.lms.project.LMS.Repository.ExamRepository;
import com.lms.project.LMS.Repository.CourseRepository;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;
    private final CourseService courseService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;  // 추가
    private final ExamRepository examRepository;      // 추가
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    @Autowired
    public ExamController(ExamService examService, 
                          CourseService courseService, 
                          MemberService memberService,
                          MemberRepository memberRepository,
                          ExamRepository examRepository,
                          CourseRepository courseRepository,
                          SubmissionRepository submissionRepository) {
        this.examService = examService;
        this.courseService = courseService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;  // 주입
        this.examRepository = examRepository;   // 주입
        this.courseRepository = courseRepository;
        this.submissionRepository=submissionRepository;
    }

    // 시험 생성 (교수만 가능)
    @PostMapping("/{memberId}/{courseId}")
    public ResponseEntity<Long> createExam(@PathVariable("memberId") Long memberId, 
                                           @PathVariable("courseId") Long courseId,
                                           @RequestBody Exam exam) {
        // memberId로 Member 조회
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1L);  // 실패 시 -1 반환
        }

        // 역할이 교수인지 확인 (대소문자 구분 없이)
        if (!"PROFESSOR".equalsIgnoreCase(member.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(-2L);  // 실패 시 -2 반환
        }

        // courseId로 Course 조회
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-3L);  // 실패 시 -3 반환
        }

        // 시험 저장
        exam.setMember(member); // 시험을 교수와 연결
        exam.setCourse(course); // 시험을 강의와 연결
        Exam savedExam = examRepository.save(exam);

        return ResponseEntity.ok(savedExam.getId());  // 생성된 시험 ID 반환
        
        
    }
    
    // ✅ 특정 시험 수정 (PUT 요청)
    @PutMapping("/{examId}")
    public ResponseEntity<?> updateExam(@PathVariable Long examId, @RequestBody Exam updatedExam, Principal principal) {
        // 현재 로그인한 교수 확인
        Member professor = memberRepository.findByStudentId(principal.getName()).orElse(null);
        if (professor == null || !"PROFESSOR".equalsIgnoreCase(professor.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("시험을 수정할 권한이 없습니다.");
        }

        Exam existingExam = examRepository.findById(examId).orElse(null);
        if (existingExam == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("시험을 찾을 수 없습니다.");
        }

        // ✅ 수정할 데이터 반영 (입력값이 존재하는 경우만 수정)
        if (updatedExam.getTitle() != null) {
            existingExam.setTitle(updatedExam.getTitle());
        }
        if (updatedExam.getDescription() != null) {
            existingExam.setDescription(updatedExam.getDescription());
        }
        if (updatedExam.getTotalPoints() != null) {
            existingExam.setTotalPoints(updatedExam.getTotalPoints());
        }
        if (updatedExam.getExamDate() != null) {
            existingExam.setExamDate(updatedExam.getExamDate());
        }
        if (updatedExam.getExamType() != null) {
            existingExam.setExamType(updatedExam.getExamType());
        }

        examRepository.save(existingExam);
        return ResponseEntity.ok("시험이 성공적으로 수정되었습니다.");
    }
    
     // ✅ 특정 시험 삭제 (DELETE 요청)
    	@DeleteMapping("/{examId}")
        public ResponseEntity<?> deleteExam(@PathVariable Long examId, Principal principal) {
            // 현재 로그인한 사용자 확인
            Member professor = memberRepository.findByStudentId(principal.getName()).orElse(null);
            if (professor == null || !"PROFESSOR".equalsIgnoreCase(professor.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("시험을 삭제할 권한이 없습니다.");
            }

            Exam exam = examRepository.findById(examId).orElse(null);
            if (exam == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("시험을 찾을 수 없습니다.");
            }

            examRepository.delete(exam);
            return ResponseEntity.ok("시험이 성공적으로 삭제되었습니다.");
        }
    
    
    // 교수의 강의 목록 조회
    @GetMapping("/professor")
    public ResponseEntity<List<CourseResponse>> getCoursesForProfessor(Principal principal) {
        String professorId = principal.getName();  
        
        // CourseService를 이용해 교수의 강의 목록 조회
        List<Course> courses = courseService.getCoursesByProfessor(professorId);
        List<CourseResponse> courseResponses = courses.stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(courseResponses);
    }

    // 모든 시험 조회
    @GetMapping
    public List<Exam> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        return exams != null ? exams : new ArrayList<>();
    }

    // 특정 강의의 시험 목록 조회
    @GetMapping("/select")
    public ResponseEntity<List<Exam>> getExamsByCourseId(@RequestParam Long courseId) {
        List<Exam> exams = examRepository.findByCourseId(courseId);
        return ResponseEntity.ok(exams);
    }


    // 특정 시험 조회
    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }
    
    @GetMapping("/{examId}/submitted-students")
    public ResponseEntity<List<Map<String, Object>>> getSubmittedStudents(@PathVariable Long examId) {
        List<Submission> submissions = submissionRepository.findByExamId(examId);
        
        List<Map<String, Object>> students = submissions.stream().map(submission -> {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", submission.getMember().getId());
            studentData.put("name", submission.getMember().getName());
            studentData.put("score", submission.getTotalScore());
            return studentData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(students);
    }
}
