package com.lms.project.LMS.Service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Enrollment;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.EnrollmentRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, 
                             MemberRepository memberRepository, 
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
    }

    // ✅ 특정 학생의 강의 등록 정보 조회
    public Enrollment studentEnroll(Long memberId, Long courseId) {
        System.out.println("🔥 [studentEnroll] 요청 들어옴 - memberId: " + memberId + ", courseId: " + courseId); // ✅ 값 확인

        if (memberId == null) {
            throw new IllegalArgumentException("❌ memberId is null");
        }
        

        // Member 조회
        Member student = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("❌ Student not found with id: " + memberId));

        // 학생인지 검증
        if (!"STUDENT".equals(student.getRole())) {  
            throw new IllegalArgumentException("❌ Only students can enroll in courses.");
        }

        // 강의 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("❌ Course not found with id: " + courseId));

        // 이미 신청한 강의인지 확인
        if (enrollmentRepository.existsByMemberIdAndCourseId(memberId, courseId)) {
            throw new IllegalArgumentException("❌ 이미 신청한 강의입니다.");
        }

        // 새로운 수강 신청 생성
        Enrollment enrollment = new Enrollment(student, course); 
        enrollment.setStatus(Enrollment.Status.PENDING); // 기본값: 대기 중
        enrollment.setStudentId(student.getStudentId()); // studentId 설정

        enrollment = enrollmentRepository.save(enrollment); // 저장 후 다시 변수에 할당
        return enrollmentRepository.save(enrollment);
    }
 // 특정 강의(courseId)에 수강 신청한 학생 목록 조회
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }



    // ✅ 교수의 수강 승인 또는 거절 (관리자처럼 모든 강의 승인/거절 가능)
    public Enrollment updateEnrollmentStatus(Long enrollmentId, Enrollment.Status status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));

        // 교수의 승인/거절 권한 확인 (관리자처럼 모든 강의 승인 가능)
        enrollment.setStatus(status); // 교수에 의해 승인 또는 거절됨
        return enrollmentRepository.save(enrollment);
    }

    // ✅ 특정 강의에서 '승인 대기(PENDING)' 상태인 신청 목록 조회 (courseId 사용)
    public List<Enrollment> getPendingEnrollmentsByCourseId(Long courseId) {
        List<Enrollment> pendingEnrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, Enrollment.Status.PENDING);
        return pendingEnrollments;
    }
    
    // ✅ 특정 학생이 신청한 강의 목록 조회
    public List<Enrollment> getEnrollmentsByMemberId(Long memberId) {
        List<Enrollment> enrollments = enrollmentRepository.findByMemberId(memberId);
        
        System.out.println("🔥 [getEnrollmentsByMemberId] 요청 - memberId: " + memberId);
        System.out.println("🔥 조회된 신청 목록: " + enrollments); // ✅ 로그 추가
        
        if (enrollments.isEmpty()) {
            System.out.println("❌ 신청된 강의가 없습니다.");
        }
        
        return enrollments;
    }
    
    @Transactional
    public void cancelEnrollment(Long memberId, Long courseId) {
        var enrollment = enrollmentRepository.findByMemberIdAndCourseId(memberId, courseId)
                .orElseThrow(() -> new EntityNotFoundException("해당 수강 신청을 찾을 수 없습니다."));
        
        enrollmentRepository.delete(enrollment);
    }

}
