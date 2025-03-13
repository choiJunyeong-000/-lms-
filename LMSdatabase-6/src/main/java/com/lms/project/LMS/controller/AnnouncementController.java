package com.lms.project.LMS.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.project.LMS.DTO.AnnouncementDto;
import com.lms.project.LMS.DTO.CourseResponse;
import com.lms.project.LMS.DTO.WeekResponse;
import com.lms.project.LMS.Entity.Announcement;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Enum.CourseStatus;
import com.lms.project.LMS.Service.AnnouncementService;
import com.lms.project.LMS.Service.AssignmentService;
import com.lms.project.LMS.Service.ContentService;
import com.lms.project.LMS.Service.CourseService;
import com.lms.project.LMS.Service.FeedbackService;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Service.WeekService;

import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@CrossOrigin(origins = "http://localhost:3000") // CORS 설정 (프론트엔드와 연결)
@RestController
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final MemberService memberService;
    private final CourseService courseService;
    private final WeekService weekService;

    @Autowired
    public AnnouncementController(AnnouncementService announcementService, MemberService memberService,
                                  CourseService courseService, WeekService weekService) {
        this.announcementService = announcementService;
        this.memberService = memberService;
        this.courseService = courseService;
        this.weekService = weekService;
    }

    // 공지사항 생성 API 
    @PostMapping("/api/courses/{courseId}/weeks/{weekNumber}/announcements")
    public ResponseEntity<?> createAnnouncement(
            @PathVariable Long courseId,
            @PathVariable int weekNumber,
            @RequestParam Long memberId,
            @RequestBody  AnnouncementDto request) {

        // 1. 회원 조회 및 권한 확인
        Optional<Member> memberOptional = memberService.findById(memberId);
        if (!memberOptional.isPresent()) {
            System.out.println("❌ 회원을 찾을 수 없음: memberId=" + memberId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
        }
        Member member = memberOptional.get();

        if (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN"))) {
            System.out.println("❌ 접근 권한 없음: memberId=" + memberId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        try {
            // 2. 강의 조회
            Optional<Course> courseOptional = courseService.findCourseById(courseId);
            if (!courseOptional.isPresent()) {
                System.out.println("❌ 강의를 찾을 수 없음: courseId=" + courseId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의를 찾을 수 없습니다.");
            }
            Course course = courseOptional.get();

            // 3. 주차 조회 (WeekService에 findByCourseIdAndWeekNumber 메서드가 있는지 확인 필요!)
            Optional<Week> weekOptional = weekService.findByCourseIdAndWeekNumber(courseId, weekNumber);
            if (!weekOptional.isPresent()) {
                System.out.println("❌ 해당 주차를 찾을 수 없음: weekNumber=" + weekNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주차를 찾을 수 없습니다.");
            }
            Week week = weekOptional.get();

            // 4. 공지사항 저장
            Announcement announcement = new Announcement();
            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());
            announcement.setCourse(course);
            announcement.setWeek(week);
            announcement.setCreatedBy(member);
            announcement.setActive(true);

            announcementService.saveAnnouncement(announcement);
            System.out.println("✅ 공지사항 저장 완료: title=" + request.getTitle());

            return ResponseEntity.ok("공지사항이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            System.out.println("❌ 공지사항 생성 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("공지사항 생성 중 오류 발생: " + e.getMessage());
        }
    }

    /** ✅ 공지사항 조회 API */
    @GetMapping("/api/courses/{courseId}/weeks/{weekNumber}/announcements")
     public ResponseEntity<?> getAnnouncements(
             @PathVariable Long courseId, 
             @PathVariable int weekNumber) {

         try {
             // 공지사항 목록 조회
             List<Announcement> announcements = announcementService.getAnnouncementsByCourseAndWeek(courseId, weekNumber);

             // 공지사항이 없다면 적절한 응답 반환
             if (announcements.isEmpty()) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주차의 공지사항이 없습니다.");
             }

             // Announcement -> AnnouncementDto로 변환
             List<AnnouncementDto> announcementDtos = announcements.stream()
                     .map(AnnouncementDto::new)  // AnnouncementDto 생성자를 사용하여 변환
                     .collect(Collectors.toList());

             // 공지사항 목록 반환
             return ResponseEntity.ok(announcementDtos);
         } catch (Exception e) {
             System.out.println("❌ 공지사항 조회 중 오류 발생: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("공지사항 조회 중 오류 발생: " + e.getMessage());
         }
     }
   // 공지사항 목록
    @GetMapping("/courses/{courseId}/announcements")
    public ResponseEntity<List<AnnouncementDto>> getAllByCourse(@PathVariable Long courseId) {
        try {
            List<AnnouncementDto> announcements = announcementService.getAllByCourse(courseId);

            if (announcements.isEmpty()) {
                return ResponseEntity.noContent().build(); // 공지사항이 없으면 204 반환
            }

            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.emptyList());
        }
    }

    // 공지사항 상세내용
    @GetMapping("/api/courses/{courseId}/announcements/{announcementId}")
    public ResponseEntity<?> getAnnouncementDetail(
            @PathVariable Long courseId, 
            @PathVariable Long announcementId) {
        try {
            Optional<Announcement> announcementOptional = announcementService.findById(announcementId);
            if (!announcementOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다.");
            }
            
            Announcement announcement = announcementOptional.get();

            // ✅ courseId 검증 추가
            if (!announcement.getCourse().getId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 강의 ID입니다.");
            }
            
            // DTO 변환
            AnnouncementDto announcementDto = new AnnouncementDto(announcement);
            return ResponseEntity.ok(announcementDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("공지사항 상세 조회 중 오류 발생: " + e.getMessage());
        }
    }

    // 공지사항 수정
    @PutMapping("/api/courses/{courseId}/announcements/{announcementId}/edit")
    public ResponseEntity<?> updateAnnouncement(
            @PathVariable Long courseId,
            @PathVariable Long announcementId,
            @RequestBody AnnouncementDto request) {  
        try {

            // ✅ 공지사항 수정 요청
            AnnouncementDto updatedAnnouncement = announcementService.updateAnnouncement(
                courseId, announcementId, request.getMemberId(), request);
            return ResponseEntity.ok(updatedAnnouncement);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 수정 중 오류가 발생했습니다.");
        }
    }


    // 공지사항 삭제
    @DeleteMapping("/api/courses/{courseId}/announcements/{announcementId}")
    public ResponseEntity<String> deleteAnnouncement(
            @PathVariable Long courseId, 
            @PathVariable Long announcementId,
            @RequestParam Long memberId) {  // ✅ 삭제 요청한 사용자 ID를 Query Parameter로 받음


        try {
            announcementService.deleteAnnouncement(courseId, announcementId, memberId);
            return ResponseEntity.ok("✅ 공지사항이 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    
 // 사용자가 작성한 공지사항 조회 API
    @GetMapping("/{memberId}/announcements")
    public ResponseEntity<?> getUserAnnouncements(@PathVariable Long memberId) {
        try {
            // 1. 사용자가 작성한 공지사항 조회
            List<Announcement> announcements = announcementService.getAnnouncementsByCreatedById(memberId);

            // 2. 공지사항이 없다면 적절한 응답 반환
            if (announcements.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("작성한 공지사항이 없습니다.");
            }

            // 3. Announcement -> AnnouncementDto 변환 (courseId 포함됨)
            List<AnnouncementDto> announcementDtos = announcements.stream()
                    .map(AnnouncementDto::new)  
                    .collect(Collectors.toList());

            // 4. 공지사항 목록 반환
            return ResponseEntity.ok(announcementDtos);
        } catch (Exception e) {
            System.out.println("❌ 공지사항 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("공지사항 조회 중 오류 발생: " + e.getMessage());
        }
    }


}