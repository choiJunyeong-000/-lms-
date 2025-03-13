package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//수정된 `EnrollmentRepository`
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

 // 특정 강의에 등록된 학생 조회
 List<Enrollment> findByCourseId(Long courseId);

 // 특정 학생(Member)이 등록된 강의 조회
 List<Enrollment> findByMemberId(Long memberId);

 // 특정 학생이 특정 강의를 수강 중인지 확인
 boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);

 // 특정 강의에서 '승인 대기(PENDING)' 상태인 신청 목록 조회
 List<Enrollment> findByCourseIdAndStatus(Long courseId, Enrollment.Status status);

 // 특정 학생이 수강 신청 대기 중인 목록 조회
 List<Enrollment> findByMemberIdAndStatus(Long memberId, Enrollment.Status status); // 추가된 부분
 
 Optional<Enrollment> findByMemberIdAndCourseId(Long memberId, Long courseId);

}
