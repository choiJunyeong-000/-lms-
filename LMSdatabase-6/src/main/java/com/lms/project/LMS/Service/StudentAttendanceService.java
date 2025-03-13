package com.lms.project.LMS.Service;

import com.lms.project.LMS.DTO.StudentAttendanceDTO;
import com.lms.project.LMS.Entity.Attendance;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.AttendanceRepository;
import com.lms.project.LMS.Repository.ContentRepository;
import com.lms.project.LMS.Repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentAttendanceService {

    private final ContentRepository contentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public List<StudentAttendanceDTO> getStudentAttendance(Long courseId, Long studentId) { // ✅ studentId를 받아서 처리
        log.info("🟢 getStudentAttendance 호출됨: courseId = {}, studentId = {}", courseId, studentId);

        // ✅ studentId로 memberId 조회
        Long memberId = memberRepository.findByStudentId(String.valueOf(studentId))
                .map(Member::getId)
                .orElseThrow(() -> new RuntimeException("해당 studentId를 가진 회원을 찾을 수 없습니다: " + studentId));

        log.info("🟢 변환된 memberId: {}", memberId);

        List<Content> contents = contentRepository.findByCourseId(courseId);
        log.info("🟢 조회된 Content 개수: {}", contents.size());

        return contents.stream().map(content -> {
            log.info("🟢 contentId = {}", content.getId());

            // ✅ 변환된 memberId를 사용하여 출석 데이터 조회
            List<Attendance> attendances = attendanceRepository.findByMemberIdAndContentId(memberId, content.getId());
            log.info("🟢 조회된 Attendance 개수 (contentId = {}): {}", content.getId(), attendances.size());

            Attendance attendance = attendances.isEmpty() ? null : attendances.get(0);

            if (attendance != null) {
                log.info("🟢 Attendance ID = {}, progress = {}, present = {}", 
                          attendance.getId(), attendance.getProgress(), attendance.isPresent());
            } else {
                log.info("⚠ 출석 정보 없음 (contentId = {})", content.getId());
            }

            return new StudentAttendanceDTO(
                    content.getWeek().getId(),
                    content.getFileName(),
                    (attendance != null) ? attendance.getProgress() : 0,
                    (attendance != null && attendance.isPresent())
            );
        }).collect(Collectors.toList());
    }
}
