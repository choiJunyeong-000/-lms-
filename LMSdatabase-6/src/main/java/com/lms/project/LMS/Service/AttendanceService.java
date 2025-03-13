package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Attendance;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Video;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Repository.AttendanceRepository;
import com.lms.project.LMS.Repository.ContentRepository;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.VideoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private ContentRepository contentRepository;

    private static final Logger logger = LoggerFactory.getLogger(LectureService.class);

    public boolean markAttendance(Member member, Course course, double progress, Content content) {
        try {
            // 출석 정보 생성
            Attendance attendance = new Attendance();
            attendance.setMember(member);
            attendance.setCourse(course);  // Video 대신 Course 설정
            attendance.setProgress(progress);
            
            // content가 null일 경우에도 문제 없도록 설정
            if (content != null) {
                attendance.setContent(content);
            } else {
                attendance.setContent(null);  // Content가 null일 경우, null로 설정
            }

            // 출석 체크
            attendance.checkAttendance();

            // 출석 정보 저장
            attendanceRepository.save(attendance);

            // 출석이 제대로 등록되었으면 true 반환
            return attendance.isPresent();

        } catch (IllegalArgumentException e) {
            // 잘못된 인자값이 들어갔을 경우 로깅하고 예외 던지기
            logger.error("출석 체크 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // 예기치 않은 오류 처리
            logger.error("출석 정보 저장 중 예기치 않은 오류 발생", e);
            throw new RuntimeException("출석 정보를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

    public boolean markCourseAttendance(String studentId, Long courseId, Long contentId, double progress) {
        try {
            // 학생과 강의를 찾는 과정
            Member member = memberRepository.findByStudentId(studentId)
                    .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
            
            // 강의의 주차별 콘텐츠를 조회 (contentId를 통해 특정 콘텐츠를 찾음)
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new RuntimeException("콘텐츠를 찾을 수 없습니다."));

            // 주차별 출석 체크
            Optional<Attendance> existingRecord = attendanceRepository.findByMemberAndContent(member, content);

            if (existingRecord.isPresent()) {
                // 기존 출석 정보가 있으면 진행 상황 업데이트
                Attendance attendance = existingRecord.get();
                attendance.setProgress(Math.max(attendance.getProgress(), progress));  // 최대 진행 상황으로 업데이트
                attendance.checkAttendance();  // 출석 여부 체크
                attendanceRepository.save(attendance);
            } else {
                // 기존 출석 정보가 없으면 새로운 출석 정보 생성
                markAttendance(member, course, progress, content);  // 출석 체크 생성
            }

            return true;  // 출석 체크 완료

        } catch (Exception e) {
            // 오류 처리
            logger.error("Course 출석 체크 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Course 출석 체크 중 오류가 발생했습니다.", e);
        }
    }



    /**
     * Course 강의 출석 체크
     */
    public boolean markContentAttendance(String studentId, Long contentId, double progress) {
        try {
            // 학생과 콘텐츠를 찾는 과정
            Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다. studentId: " + studentId));

            Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("콘텐츠를 찾을 수 없습니다. contentId: " + contentId));

            // content가 null이 아닌지 확인 (비상 상황 처리)
            if (content == null) {
                throw new IllegalArgumentException("콘텐츠가 존재하지 않습니다.");
            }

            // 출석 정보를 조회
            Optional<Attendance> existingRecord = attendanceRepository.findByMemberAndContent(member, content);

            if (existingRecord.isPresent()) {
                // 기존 출석 정보가 있으면 진행 상황 업데이트
                Attendance attendance = existingRecord.get();
                attendance.setProgress(Math.max(attendance.getProgress(), progress));
                attendance.checkAttendance();  // 출석 여부 체크
                attendanceRepository.save(attendance);
                return attendance.isPresent();
            } else {
                // 기존 출석 정보가 없으면 새로운 출석 정보 생성
                return markAttendance(member, content.getCourse(), progress, content); // Content에 맞는 출석 정보 생성
            }
        } catch (IllegalArgumentException e) {
            // 예외 처리: 구체적인 오류 메시지 로깅
            logger.error("출석 체크 실패: {}", e.getMessage());
            throw e; // 예외 던지기
        } catch (Exception e) {
            // 예상치 못한 오류 처리
            logger.error("출석 체크 중 예기치 않은 오류 발생", e);
            throw new RuntimeException("출석 체크 중 예기치 않은 오류가 발생했습니다.");
        }
    }



    /**
     * Video 강의 출석 체크
     */
    public boolean markVideoAttendance(String studentId, Long videoId, double progress) {
        try {
            // 학생과 비디오를 찾는 과정
            Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다. studentId: " + studentId));

            Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("동영상을 찾을 수 없습니다. videoId: " + videoId));

            // Video에 해당하는 Content를 조회, 없으면 null로 처리
            Content content = contentRepository.findByVideo(video).orElse(null);  // content가 없으면 null 반환

            // 출석 정보를 조회
            Optional<Attendance> existingRecord = attendanceRepository.findByMemberAndVideo(member, video);

            if (existingRecord.isPresent()) {
                // 기존 출석 정보가 있으면 진행 상황 업데이트
                Attendance attendance = existingRecord.get();
                attendance.setProgress(Math.max(attendance.getProgress(), progress));
                attendance.checkAttendance();  // 출석 여부 체크
                attendanceRepository.save(attendance);
                return attendance.isPresent();
            } else {
                // 기존 출석 정보가 없으면 새로운 출석 정보 생성
                return markAttendance(member, video.getCourse(), progress, content); // content가 없으면 null 처리
            }
        } catch (IllegalArgumentException e) {
            // 예외 처리: 구체적인 오류 메시지 로깅
            logger.error("출석 체크 실패: {}", e.getMessage());
            throw e; // 예외 던지기
        } catch (Exception e) {
            // 예상치 못한 오류 처리
            logger.error("출석 체크 중 예기치 않은 오류 발생", e);
            throw new RuntimeException("출석 체크 중 예기치 않은 오류가 발생했습니다.");
        }
    }



 
    // 출석 상태 조회 메서드 추가
    public Map<Long, Boolean> getAttendanceStatus(String studentId) {
        Map<Long, Boolean> attendanceStatus = new HashMap<>();
        List<Attendance> attendances = attendanceRepository.findByMember_StudentId(studentId);
        for (Attendance attendance : attendances) {
            attendanceStatus.put(attendance.getVideo().getId(), attendance.isAttended());
        }
        return attendanceStatus;
    }

    // 진도율 저장 메서드 수정
    public void saveProgress(String studentId, Long videoId, double watchedPercentage) {
        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid video ID"));
        Course course = video.getCourse();

        Attendance attendance = attendanceRepository.findByMember_StudentIdAndVideo_Id(studentId, videoId)
                .orElse(new Attendance());
        attendance.setMember(member);
        attendance.setVideo(video);
        attendance.setCourse(course);

        // 현재 저장된 진도율보다 클 경우에만 업데이트
        if (watchedPercentage > attendance.getWatchedPercentage()) {
            attendance.setWatchedPercentage(watchedPercentage);
        }

        // 출석 여부 체크
        if (attendance.getWatchedPercentage() >= 90) {
            attendance.setAttended(true);
        }

        attendanceRepository.save(attendance);
    }

    // 진도율 상태 조회 메서드 추가
    public Map<Long, Double> getProgressStatus(String studentId) {
        Map<Long, Double> progressStatus = new HashMap<>();
        List<Attendance> attendances = attendanceRepository.findByMember_StudentId(studentId);
        for (Attendance attendance : attendances) {
            progressStatus.put(attendance.getVideo().getId(), attendance.getWatchedPercentage());
        }
        return progressStatus;
    }
    
    
}