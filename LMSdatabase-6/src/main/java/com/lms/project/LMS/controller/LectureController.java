package com.lms.project.LMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.project.LMS.Entity.Video;
import com.lms.project.LMS.Service.LectureService;
import com.lms.project.LMS.DTO.AttendanceRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;

    // 비디오 업로드 API
    @PostMapping("/upload-video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file, 
                                               @RequestParam("courseName") String courseName,
                                               @RequestParam("courseDescription") String courseDescription) {
        try {
            // 로그인한 사용자 정보에서 studentId 추출
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName(); // 이 부분은 실제 로그인 사용자 정보를 반환합니다.

            // 서비스 메소드 호출 (saveVideo 메소드를 호출하여 비디오를 저장)
            Video video = lectureService.saveVideo(file, courseName, courseDescription, studentId);
            
            // 업로드된 비디오 URL을 포함하여 성공 메시지 반환
            return ResponseEntity.ok("파일 업로드 성공: " + video.getVideoUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }

    // 비디오 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<Video>> getVideoList() {
        // lectureService에서 비디오 목록을 가져옴
        List<Video> videoList = lectureService.getAllVideos();
        
        // course_id가 null인 비디오를 필터링
        videoList = videoList.stream()
                             .filter(video -> video.getCourseId() != null)
                             .collect(Collectors.toList());

        return ResponseEntity.ok(videoList);
    }



    // 비디오 삭제 API
    @DeleteMapping("/delete-video/{videoId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long videoId) {
        try {
            lectureService.deleteVideo(videoId);
            return ResponseEntity.ok("비디오 삭제 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비디오 삭제 실패");
        }
    }

    // 출석 처리 API
    @PostMapping("/mark-attendance")
    public ResponseEntity<String> markAttendance(@RequestBody AttendanceRequest request) {
        try {
            // 출석 처리 메소드 호출 (필드명에 맞게 수정)
            boolean isMarked = lectureService.markAttendance(request.getStudentId(), request.getCourseId(), request.getWatchedPercentage());
            if (isMarked) {
                return ResponseEntity.ok("출석 완료");
            } else {
                return ResponseEntity.ok("진도 저장됨");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("출석 처리 실패");
        }
    }
}
