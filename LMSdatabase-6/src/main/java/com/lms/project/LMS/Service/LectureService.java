package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Attendance;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Video;
import com.lms.project.LMS.Repository.AttendanceRepository;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.VideoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LectureService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final String uploadDir = "C:/uploads/";  // 업로드된 비디오 파일 저장 경로
    private static final Logger logger = LoggerFactory.getLogger(LectureService.class);

    // 비디오 파일 저장 및 DB 저장
    public Video saveVideo(MultipartFile file, String courseName, String courseDescription, String studentId) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        try {
            // 파일이 비어있는지 확인
            if (file.isEmpty()) {
                logger.error("업로드된 파일이 비어 있습니다.");
                throw new RuntimeException("업로드된 파일이 비어 있습니다.");
            }

            // 디렉토리 존재 여부 확인 후, 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                if (!dirCreated) {
                    logger.error("디렉토리 생성에 실패했습니다: {}", uploadDir);
                    throw new RuntimeException("디렉토리 생성 실패.");
                }
            }

            // 파일을 지정된 경로에 저장
            File videoFile = new File(filePath);
            file.transferTo(videoFile);  // 파일 저장
            logger.info("파일 업로드 성공: {}", fileName);

            // 새로운 강의 객체 생성
            Course course = new Course();
            course.setName(courseName);
            course.setDescription(courseDescription);
            course.setStartDate(LocalDate.now());  // 시작일을 현재 날짜로 설정
            course.setEndDate(LocalDate.now().plusMonths(3));  // 종료일 기본값 (예: 3개월 후)

            // studentId를 이용해 member를 조회하고, 강의에 연결
            Member member = memberRepository.findByStudentId(studentId).orElseThrow(() -> 
                    new RuntimeException("회원 정보를 찾을 수 없습니다."));
            
            course.setMember(member);  // 강의에 해당 member 할당

            // 강의 저장 후 course_id 자동 생성
            course = courseRepository.save(course);

            // 비디오 객체 생성 및 강의와 연결
            Video video = new Video();
            video.setVideoUrl("uploads/" + fileName);  // 저장된 파일 경로를 비디오 URL로 설정
            video.setCourse(course);  // 새로 생성된 강의와 비디오 연결

            // 비디오 정보 저장
            video = videoRepository.save(video);

            return video;

        } catch (IOException e) {
            logger.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            throw new IOException("파일 업로드 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("비디오 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("비디오 저장 중 오류가 발생했습니다.", e);
        }
    }



    // 모든 비디오 목록 조회
    public List<Video> getAllVideos() {
        return videoRepository.findAll();  // 비디오 목록 조회
    }

 // 비디오 삭제
    public void deleteVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("비디오가 존재하지 않습니다."));

        // 관련된 출석 기록 삭제
        List<Attendance> attendances = attendanceRepository.findByVideoId(videoId);
        attendanceRepository.deleteAll(attendances);

        // 비디오 파일 삭제
        File file = new File(video.getVideoUrl());
        if (file.exists()) {
            file.delete();
        }

        // DB에서 비디오 삭제
        videoRepository.delete(video);
    }

    // 출석 정보 조회 및 출석 상태 체크
    public void checkAttendance(String studentId, Long courseId) {
        // 출석 정보 조회
    	Attendance attendance = attendanceRepository.findByMember_StudentIdAndCourse_Id(studentId, courseId)
    	        .stream()
    	        .findFirst()
    	        .orElseThrow(() -> new RuntimeException("출석 정보가 없습니다."));


        // 출석 기준: progress가 90 이상이면 출석 인정
        if (attendance.getProgress() >= 90) {
            attendance.setPresent(true);  // 출석 상태 true로 설정
        } else {
            attendance.setPresent(false);  // 출석 상태 false로 설정
        }

        attendanceRepository.save(attendance);  // 수정된 출석 정보 저장
    }

    // 출석 정보를 추가하는 메소드
    public Attendance addAttendance(Long memberId, Long courseId, double progress) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
        
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCourse(course);
        attendance.setProgress(progress);

        // 출석 체크
        attendance.checkAttendance();  // progress에 따른 출석 여부 자동 설정
        
        return attendanceRepository.save(attendance);
    }

    // 특정 강의의 모든 출석 정보 조회
    public List<Attendance> getAttendanceByCourse(Long courseId) {
        return attendanceRepository.findByCourseId(courseId);  // 강의별 출석 정보 조회
    }

    // 특정 학생의 모든 출석 정보 조회
    public List<Attendance> getAttendanceByMember(String studentId) {
        return attendanceRepository.findByMember_StudentId(studentId);  // 학생별 출석 정보 조회
    }

    // 출석을 마킹하는 메소드
    public boolean markAttendance(String studentId, Long courseId, double progress) {
        // 학생과 강의 정보 조회
        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));

        // 해당 학생과 강의에 대한 출석 정보 조회
        Optional<Attendance> existingRecord = attendanceRepository.findByMemberAndCourse(member, course);

        // 출석 기록이 있으면 진행 상황을 업데이트하고 출석 체크
        if (existingRecord.isPresent()) {
            Attendance attendance = existingRecord.get();
            attendance.setProgress(Math.max(attendance.getProgress(), progress));  // 기존 값보다 큰 값 저장
            attendance.checkAttendance();  // 출석 체크
            attendanceRepository.save(attendance);  // 수정된 출석 정보 저장
            return attendance.getProgress() >= 90;  // 출석 여부 (90% 이상이면 출석 인정)
        } else {
            // 출석 기록이 없으면 새로 추가
            Attendance newAttendance = new Attendance();
            newAttendance.setMember(member);
            newAttendance.setCourse(course);
            newAttendance.setProgress(progress);
            newAttendance.checkAttendance();  // 출석 체크
            attendanceRepository.save(newAttendance);  // 출석 정보 DB에 저장
            return newAttendance.getProgress() >= 90;  // 출석 여부 (90% 이상이면 출석 인정)
        }
    }
}
