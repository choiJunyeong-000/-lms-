package com.lms.project.LMS.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.project.LMS.DTO.CourseResponse;
import com.lms.project.LMS.DTO.WeekResponse;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Enum.CourseStatus;
import com.lms.project.LMS.Service.AnnouncementService;
import com.lms.project.LMS.Service.AssignmentService;
import com.lms.project.LMS.Service.ContentService;
import com.lms.project.LMS.Service.CourseQnAService;
import com.lms.project.LMS.Service.CourseService;
import com.lms.project.LMS.Service.ExamService;
import com.lms.project.LMS.Service.FeedbackService;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Service.WeekService;

import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000") // CORS 설정 (프론트엔드와 연결)
public class CourseController {

    private final CourseService courseService;
    private final ContentService contentService;
    private final FeedbackService feedbackService;
    private final AnnouncementService announcementService;
    private final MemberService memberService;
    private final AssignmentService assignmentService;
    private final WeekService weekService;
    private final CourseQnAService courseQnAService;
    private final ExamService examService;
  

    @Value("${file.upload-dir}")
    private String uploadDir = "uploads";;

    public CourseController(CourseService courseService, WeekService weekService, ContentService contentService,
                            FeedbackService feedbackService, AnnouncementService announcementService, MemberService memberService,
                            AssignmentService assignmentService, CourseQnAService courseQnAService, ExamService examService) {
        this.courseService = courseService;
        this.contentService = contentService;
        this.weekService = weekService;
        this.feedbackService = feedbackService;
        this.announcementService = announcementService;
        this.memberService = memberService;
        this.assignmentService = assignmentService;
        this.courseQnAService = courseQnAService;
        this.examService = examService;
    }

    // 강의 추가
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestParam Long memberId, @RequestBody Course course) {
        Optional<Member> memberOptional = memberService.findById(memberId);
        if (!memberOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
        }
        Member member = memberOptional.get();

        // MemberRole을 String으로 비교 (PROFESSOR 또는 ADMIN)
        if (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        try {
            Course savedCourse = courseService.saveCourse(course);

            // 1~15주차 자동 생성
            for (int i = 1; i <= 15; i++) {
                Week week = new Week();
                week.setWeekNumber(i);
                week.setCourse(savedCourse);
                weekService.save(week);
            }

            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("강의 추가 중 오류 발생: " + e.getMessage());
        }
    }


    // 강의 삭제
    @DeleteMapping("/{courseId}/weeks/{weekNumber}/content/{contentId}") //콘텐츠 삭제
    public ResponseEntity<?> deleteContent(
            @PathVariable Long courseId,
            @PathVariable int weekNumber,
            @PathVariable Long contentId,
            @RequestParam Long memberId) {

        // 1. 회원 조회 및 권한 확인
        Member member = memberService.findById(memberId).orElse(null);
        if (member == null) {
            System.out.println("❌ 회원을 찾을 수 없음: memberId=" + memberId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
        }

        if (member.getRole() == null || 
            (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN")))) {
            System.out.println("❌ 접근 권한 없음: memberId=" + memberId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        // 2. 콘텐츠 조회
        Content content = contentService.findById(contentId).orElse(null);
        if (content == null) {
            System.out.println("❌ 콘텐츠를 찾을 수 없음: contentId=" + contentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("콘텐츠를 찾을 수 없습니다.");
        }

        // 3. 강의 및 주차 정보 확인
        if (!content.getCourse().getId().equals(courseId) || content.getWeek().getWeekNumber() != weekNumber) {
            System.out.println("❌ 콘텐츠가 해당 강의 및 주차에 속하지 않음");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        }

        try {
            // 4. 파일 삭제
            File file = new File(content.getFilePath());
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("✅ 파일 삭제 완료: " + content.getFilePath());
                } else {
                    System.out.println("⚠️ 파일 삭제 실패: " + content.getFilePath());
                }
            } else {
                System.out.println("⚠️ 파일이 존재하지 않음: " + content.getFilePath());
            }

            // 5. DB에서 콘텐츠 삭제
            contentService.deleteContent(content);
            System.out.println("✅ 콘텐츠 삭제 완료: contentId=" + contentId);

            return ResponseEntity.ok("콘텐츠가 성공적으로 삭제되었습니다.");

        } catch (Exception e) {
            System.out.println("❌ 콘텐츠 삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("콘텐츠 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    // 강의 상세 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long courseId) {
        Optional<Course> courseOptional = courseService.findCourseById(courseId);
        if (!courseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(new CourseResponse(courseOptional.get()));
    }

    // 전체 강의 목록 조회
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourses() {
        List<CourseResponse> courses = courseService.getAllCourses().stream().map(CourseResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    // 강의 콘텐츠 업로드
    @PostMapping("/{courseId}/weeks/{weekNumber}/content")
    public ResponseEntity<?> uploadContent(
            @PathVariable Long courseId,
            @PathVariable int weekNumber,
            @RequestParam Long memberId,
            @RequestParam("file") MultipartFile file) {


        // 1. 회원 조회 및 권한 확인
        Member member = memberService.findById(memberId)
                .orElse(null);
        if (member == null) {
            System.out.println("❌ 회원을 찾을 수 없음: memberId=" + memberId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
        }

        // 2. 권한 확인
        if (member.getRole() == null || 
            (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN")))) {
            System.out.println("❌ 접근 권한 없음: memberId=" + memberId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        try {
            // 3. 강의 조회
            Course course = courseService.findCourseById(courseId)
                    .orElse(null);
            if (course == null) {
                System.out.println("❌ 강의를 찾을 수 없음: courseId=" + courseId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의를 찾을 수 없습니다.");
            }

            // 4. 주차 조회
            Week week = weekService.findByCourseIdAndWeekNumber(courseId, weekNumber)
                    .orElse(null);
            if (week == null) {
                System.out.println("❌ 해당 주차를 찾을 수 없음: weekNumber=" + weekNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주차를 찾을 수 없습니다.");
            }

            // 🛠 week 객체가 올바르게 조회되었는지 확인
            System.out.println("📌 찾은 주차 정보: weekId=" + week.getId() + ", weekNumber=" + week.getWeekNumber());

            // 5. 파일 검증
            if (file.isEmpty()) {
                System.out.println("❌ 업로드된 파일이 비어 있음.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업로드된 파일이 비어 있습니다.");
            }

            // 6. 파일 저장 경로 설정
            String uploadDir = "C:/uploads"; // 절대 경로로 수정 (서버의 적절한 경로 설정)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // 디렉토리가 없으면 생성
            }

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFileName;
            String filePath = uploadDir + File.separator + uniqueFileName;

            // 파일 저장
            File destFile = new File(filePath);
            file.transferTo(destFile);
            System.out.println("✅ 파일 저장 완료: " + filePath);

            // 7. 콘텐츠 저장
            Content content = new Content();
            content.setFileName(uniqueFileName);
            content.setFilePath(filePath);
            content.setFileType(file.getContentType());
            content.setWeek(week);
            content.setCourse(course);
            content.setMemberId(memberId); // memberId 추가

  

            contentService.saveContent(content);  // 콘텐츠 저장
            System.out.println("✅ 콘텐츠 저장 완료: fileName=" + uniqueFileName);

            // 8. 클라이언트에 반환할 파일 URL
            String fileUrl = "/uploads/" + uniqueFileName;

            return ResponseEntity.ok(fileUrl);  // 파일 URL 반환

        } catch (Exception e) {
            System.out.println("❌ 콘텐츠 업로드 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("콘텐츠 업로드 중 오류 발생: " + e.getMessage());
        }
    }
    @GetMapping("/{courseId}/weeks/contents")
    public ResponseEntity<?> getCourseContents(@PathVariable Long courseId) {
        List<Week> weeks = weekService.getWeeksForCourse(courseId);
        if (weeks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 강의에 주차가 존재하지 않습니다.");
        }

        Map<Integer, List<Map<String, Object>>> weekContents = new HashMap<>();
        for (Week week : weeks) {
            List<Content> contents = contentService.findByWeekId(week.getId());

            List<Map<String, Object>> contentList = contents.stream().map(content -> {
                Map<String, Object> contentInfo = new HashMap<>();
                contentInfo.put("id", content.getId());  
                contentInfo.put("fileName", content.getFileName());
                contentInfo.put("filePath", "http://localhost:8090/api/files/" + content.getFileName());
                contentInfo.put("title", content.getTitle());
                return contentInfo;
            }).collect(Collectors.toList());

            weekContents.put(week.getWeekNumber(), contentList);
        }

        return ResponseEntity.ok(weekContents);
    }
 // 동영상 업로드 API
    @PostMapping("/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.");
        }

        try {
            // 업로드 디렉토리 확인 및 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 파일명 생성
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            File destFile = new File(dir, fileName);

            // 파일 저장
            file.transferTo(destFile);

            // 파일 접근 URL 생성
            String fileDownloadUri = "http://localhost:8090/uploads/" + fileName;

            // 응답 반환
            return ResponseEntity.ok().body(Map.of(
                "message", "파일 업로드 성공!",
                "videoUrl", fileDownloadUri
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패: " + e.getMessage());
        }
    }
    // 강의 신청
    @PostMapping("/request")
    public ResponseEntity<?> requestCourse(Principal principal,
                                           @RequestParam("Name") String Name,
                                           @RequestParam("courseType") String courseType,
                                           @RequestParam(value = "file", required = false) MultipartFile file) {
        System.out.println("courseType: " + courseType);  // courseType 로그 출력

        String studentId = principal.getName();  // studentId 가져오기
        Member member = memberService.findUserByStudentId(studentId);
        if (member == null) {
            throw new RuntimeException("회원을 찾을 수 없습니다.");
        }
        if (!member.getRole().equals("PROFESSOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("교수만 강의를 등록 신청할 수 있습니다.");
        }

        try {
            Course course = new Course();
            course.setName(Name);
            course.setCourseType(courseType);
            course.setStatus(CourseStatus.OPEN);
            course.setProfessor(member);
            course.setProfessorStudentId(member.getStudentId()); // 교수 이름 설정

            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                String filePath = uploadDir + File.separator + fileName;
                File destFile = new File(filePath);
                
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                file.transferTo(destFile);
                course.setSyllabusFilePath(filePath);
            }

            Course requestedCourse = courseService.saveCourse(course);

            return ResponseEntity.ok(requestedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("강의 신청 중 오류 발생: " + e.getMessage());
        }
    }


    // 강의 수락
    @PutMapping("/{courseId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveCourse(@PathVariable Long courseId) {
        Optional<Course> courseOptional = courseService.findCourseById(courseId);
        if (!courseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의를 찾을 수 없습니다.");
        }
        Course course = courseOptional.get();

        course.setStatus(CourseStatus.COMPLETED);
        courseService.saveCourse(course);

        List<Week> existingWeeks = weekService.findByCourseId(courseId);
        Set<Integer> existingWeekNumbers = existingWeeks.stream()
                .map(Week::getWeekNumber)
                .collect(Collectors.toSet());

        List<Week> weeksToAdd = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            if (!existingWeekNumbers.contains(i)) { // 중복 확인
                Week week = new Week();
                week.setWeekNumber(i);
                week.setCourse(course);
                weeksToAdd.add(week);
            }
        }
        weekService.saveAll(weeksToAdd);

        return ResponseEntity.ok("강의가 승인되었습니다.");
    }

    // 강의 거절
    @Transactional
    @PutMapping("/{courseId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelCourse(@PathVariable Long courseId) {
        Optional<Course> courseOptional = courseService.findCourseById(courseId);
        if (!courseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의를 찾을 수 없습니다.");
        }
        Course course = courseOptional.get();

        // course_qna 삭제
        courseQnAService.deleteQnAByCourseId(courseId);

        // 강의 삭제
        courseService.deleteCourse(course);

        return ResponseEntity.ok("강의가 취소되고 삭제되었습니다.");
    }



    // 강의 계획서 다운로드
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileUrl) {
        try {
            // URL 디코딩
            String decodedFileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
            
            // Windows 경로 구분자 \를 /로 변환
            String filePath = decodedFileUrl.replace("\\", "/");

            // 파일이 존재하는지 확인
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 파일 이름 처리
            String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) { // 일반 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/uploads/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // 교수의 강의 목록 조회
    @GetMapping("/professor")
    public ResponseEntity<List<CourseResponse>> getCoursesForProfessor(Principal principal) {
        String studentId = principal.getName();  // principal에서 학번을 가져옵니다.
        
        // 교수의 학번으로 강의를 조회
        List<Course> courses = courseService.getCoursesByProfessor(studentId);
        List<CourseResponse> courseResponses = courses.stream()
                .map(CourseResponse::new)  // CourseResponse로 변환
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(courseResponses);
    }


    @GetMapping("/{courseId}/weeks")
    public ResponseEntity<List<WeekResponse>> getWeeksForCourse(@PathVariable Long courseId) {
        List<Week> weeks = weekService.getWeeksForCourse(courseId);
        List<WeekResponse> weekResponses = weeks.stream()
                .map(WeekResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(weekResponses);
    }

    @GetMapping("/{courseId}/exams")
    public List<Exam> getExamsByCourseId(@PathVariable Long courseId) {
        return examService.getExamsByCourseId(courseId);
    }

}









