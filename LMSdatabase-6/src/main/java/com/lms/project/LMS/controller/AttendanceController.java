package com.lms.project.LMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.DTO.AttendanceRequest;
import com.lms.project.LMS.Service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.Map;

//출석 관련 HTTP 요청을 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:3000") // React CORS 설정
public class AttendanceController {

 private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

 @Autowired
 private AttendanceService attendanceService;

 /**
  * 출석 체크 API (강의 출석)
  */
 @PostMapping("/course")
 public ResponseEntity<String> markCourseAttendance(@Valid @RequestBody AttendanceRequest request) {
     // 로그 추가: 요청 내용 출력
     logger.info("Received course attendance request: studentId={}, courseId={}, contentId={}, watchedPercentage={}", 
                 request.getStudentId(), request.getCourseId(), request.getContentId(), request.getWatchedPercentage());

     try {
         // course 관련 필드 검증
         request.validateForCourse();  

         // 출석 체크를 위해 서비스 메서드 호출
         boolean isMarked = attendanceService.markCourseAttendance(
             request.getStudentId(),  // 학생 ID
             request.getCourseId(),   // 강의 ID
             request.getContentId(),  // 콘텐츠 ID
             request.getWatchedPercentage() // 강의 시청률
         );

         // 로그 추가: 출석 완료 또는 진도 저장 여부
         logger.info("Course attendance marked: {}", isMarked ? "Success" : "Progress saved");

         // 출석 완료 여부에 따라 다른 응답 반환
         return ResponseEntity.ok(isMarked ? "출석 완료" : "진도 저장됨");

     } catch (IllegalArgumentException e) {
         // 로그 추가: 오류 발생
         logger.error("Error in course attendance: {}", e.getMessage());
         return ResponseEntity.badRequest().body(e.getMessage());
     } catch (Exception e) {
         // 예기치 않은 오류 처리
         logger.error("Unexpected error in course attendance: {}", e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body("출석 체크 중 예기치 않은 오류가 발생했습니다.");
     }
 }



 /**
  * 출석 체크 API (동영상 출석)
  */
 @PostMapping("/video")
 public ResponseEntity<String> markVideoAttendance(@RequestBody AttendanceRequest request) {
     try {
         String studentId = request.getStudentId();  // studentId를 String 타입으로 처리
         boolean isMarked = attendanceService.markVideoAttendance(
             studentId,
             request.getVideoId(),
             request.getWatchedPercentage()
         );

         if (isMarked) {
             return ResponseEntity.ok("출석 완료");
         } else {
             return ResponseEntity.ok("진도 저장됨");
         }
     } catch (Exception e) {
         return ResponseEntity.internalServerError().body("출석 처리 실패: " + e.getMessage());
     }
 }

 /**
  * 출석 상태 조회 API
  */
 @GetMapping("/status")
 public ResponseEntity<Map<Long, Boolean>> getAttendanceStatus(@RequestParam String studentId) {
     try {
         Map<Long, Boolean> attendanceStatus = attendanceService.getAttendanceStatus(studentId);
         return ResponseEntity.ok(attendanceStatus);
     } catch (Exception e) {
         return ResponseEntity.internalServerError().body(null);
     }
 }

 /**
  * 진도율 상태 조회 API
  */
 
 
 @GetMapping("/progress/status")
 public ResponseEntity<Map<Long, Double>> getProgressStatus(@RequestParam String studentId) {
     try {
         Map<Long, Double> progressStatus = attendanceService.getProgressStatus(studentId);
         return ResponseEntity.ok(progressStatus);
     } catch (Exception e) {
         return ResponseEntity.internalServerError().body(null);
     }
 }

 /**
  * 진도율 저장 API
  */
 @PostMapping("/progress/video")
 public ResponseEntity<String> saveProgress(@RequestBody AttendanceRequest request) {
     try {
         attendanceService.saveProgress(request.getStudentId(), request.getVideoId(), request.getWatchedPercentage());
         return ResponseEntity.ok("진도율 저장 완료");
     } catch (Exception e) {
         return ResponseEntity.internalServerError().body("진도율 저장 실패: " + e.getMessage());
     }
 }
}