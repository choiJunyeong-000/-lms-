
package com.lms.project.LMS.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.project.LMS.DTO.EnrollmentDTO;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Enrollment;
import com.lms.project.LMS.Service.CourseService;
import com.lms.project.LMS.Service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

   private final EnrollmentService enrollmentService;
   private final CourseService courseService;

   @Autowired
   public EnrollmentController(EnrollmentService enrollmentService, CourseService courseService) {
      this.enrollmentService = enrollmentService;
      this.courseService = courseService;
   }

   // 학생 수강 신청
   @PostMapping
   public ResponseEntity<?> enrollStudent(@RequestBody Map<String, Long> requestData) {
      try {
         Long memberId = requestData.get("memberId");
         Long courseId = requestData.get("courseId");

         if (memberId == null || courseId == null) {
            return ResponseEntity.badRequest().body("학생 ID 또는 강의 ID가 누락되었습니다.");
         }

         Enrollment enrollment = enrollmentService.studentEnroll(memberId, courseId);
         return ResponseEntity.ok(enrollment);
      } catch (Exception e) {
         return ResponseEntity.status(400).body("수강신청 중 오류 발생: " + e.getMessage());
      }
   }

   // 특정 학생이 신청한 강의 목록 조회
   @GetMapping("/member/{memberId}")
   public ResponseEntity<?> getEnrollmentsByMember(@PathVariable Long memberId) {
      try {
         List<Enrollment> enrollments = enrollmentService.getEnrollmentsByMemberId(memberId);

         List<EnrollmentDTO> enrollmentDTOs = enrollments.stream().map(enrollment -> new EnrollmentDTO(enrollment))
               .collect(Collectors.toList());

         return ResponseEntity.ok(enrollmentDTOs);
      } catch (Exception e) {
         return ResponseEntity.status(400).body("수강 신청 목록 조회 중 오류 발생: " + e.getMessage());
      }
   }
// 특정 강의를 수강 신청한 학생 목록 조회
@GetMapping("/course/{courseId}/students")
public ResponseEntity<?> getEnrolledStudentsByCourse(@PathVariable Long courseId) {
    try {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        List<EnrollmentDTO> enrolledStudents = enrollments.stream()
                .map(enrollment -> new EnrollmentDTO(enrollment))
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrolledStudents);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("수강 신청한 학생 목록 조회 중 오류 발생: " + e.getMessage());
    }
}


   // 교수의 수강 승인
   @PutMapping("/{enrollmentId}/accept")
   public ResponseEntity<?> approveEnrollment(@PathVariable Long enrollmentId) {
      try {
         Enrollment enrollment = enrollmentService.updateEnrollmentStatus(enrollmentId, Enrollment.Status.APPROVED);
         return ResponseEntity.ok(enrollment);
      } catch (Exception e) {
         return ResponseEntity.status(400).body("수강 승인 중 오류 발생: " + e.getMessage());
      }
   }

   // 교수의 수강 거절
   @PutMapping("/{enrollmentId}/reject")
   public ResponseEntity<?> rejectEnrollment(@PathVariable Long enrollmentId) {
      try {
         Enrollment enrollment = enrollmentService.updateEnrollmentStatus(enrollmentId, Enrollment.Status.REJECTED);
         return ResponseEntity.ok(enrollment);
      } catch (Exception e) {
         return ResponseEntity.status(400).body("수강 거절 중 오류 발생: " + e.getMessage());
      }
   }

   @GetMapping("/course/{courseId}/pending-enrollments")
   public ResponseEntity<?> getPendingEnrollments(@PathVariable Long courseId, Principal principal) {
       if (courseId == null || courseId <= 0) {
           return ResponseEntity.badRequest().body("❌ 강의 ID가 유효하지 않습니다.");
       }

       Optional<Course> courseOptional = courseService.findCourseById(courseId);
       if (!courseOptional.isPresent()) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("강의를 찾을 수 없습니다.");
       }

       Course course = courseOptional.get();
       String professorName = course.getProfessorStudentId();
       String currentUsername = principal.getName(); // 로그인한 사용자의 username
       
       System.out.println("📌 현재 로그인한 교수: " + currentUsername);
       System.out.println("📌 해당 강의의 담당 교수: " + professorName);

       if (professorName == null || !professorName.equals(currentUsername)) {
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이 강의에 대한 승인 권한이 없습니다.");
       }

       try {
           List<Enrollment> pendingEnrollments = enrollmentService.getPendingEnrollmentsByCourseId(courseId);
           return ResponseEntity.ok(pendingEnrollments);
       } catch (Exception e) {
           return ResponseEntity.status(400).body("승인 대기 목록 조회 중 오류 발생: " + e.getMessage());
       }
   }

      // ✅ 특정 학생의 수강 신청 취소 (DELETE 요청)
    @DeleteMapping("/member/{memberId}/course/{courseId}")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Long memberId, @PathVariable Long courseId) {
        enrollmentService.cancelEnrollment(memberId, courseId);
        return ResponseEntity.ok("수강 신청이 취소되었습니다.");
    }

}
