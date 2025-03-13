package com.lms.project.LMS.Service;

import com.lms.project.LMS.DTO.StudentManageDTO;
import com.lms.project.LMS.Entity.Attendance;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Enrollment;
import com.lms.project.LMS.Repository.AttendanceRepository;
import com.lms.project.LMS.Repository.ContentRepository;
import com.lms.project.LMS.Repository.StudentManageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentManageService {

    private final StudentManageRepository studentManageRepository;
    private final AttendanceRepository attendanceRepository;
    private final ContentRepository contentRepository; // ✅ Content 테이블 조회 추가

    public List<StudentManageDTO> getApprovedEnrollments(String professorStudentId) {
        List<Enrollment> enrollments = studentManageRepository.findApprovedEnrollmentsByProfessorStudentId(professorStudentId);
        
        return enrollments.stream().map(e -> {
            // ✅ 해당 학생의 출석 정보 가져오기
            List<Attendance> attendanceList = attendanceRepository.findByMember_StudentIdAndCourse_Id(
                    e.getStudentId(), e.getCourse().getId()
            );

            // ✅ 해당 강의의 총 Content 개수 가져오기
            long totalContentCount = contentRepository.countByCourseId(e.getCourse().getId());

            // ✅ 출석한 개수 (present == true)
            long presentCount = attendanceList.stream().filter(Attendance::isPresent).count();

            // ✅ 출석률 계산
            double attendanceRate = (totalContentCount > 0) ? ((double) presentCount / totalContentCount) * 100 : 0.0;

            return new StudentManageDTO(
                    e.getCourse().getId(),
                    e.getCourse().getName(),
                    e.getMember().getName(),
                    e.getMember().getStudentId(),
                    Math.round(attendanceRate * 100.0) / 100.0 // ✅ 소수점 두 자리 반올림
            );
        }).collect(Collectors.toList());
    }
}
