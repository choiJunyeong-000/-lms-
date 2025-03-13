package com.lms.project.LMS.Repository; // 네 패키지로 변경

import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Entity.Exam;

import com.lms.project.LMS.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 과제 및 시험 제출 데이터를 관리하는 리포지토리
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

   /**
    * 특정 시험(exam)에 대한 제출 정보 조회
    * 
    * @param exam 시험 객체
    * @return 해당 시험에 제출된 목록
    */
   List<Submission> findByExam(Exam exam);

   /**
    * 특정 학생(member)에 대한 제출 정보 조회
    * 
    * @param member 학생 객체
    * @return 해당 학생이 제출한 모든 제출 목록
    */
   List<Submission> findByMember(Member member);

   /**
    * 특정 과제(assignment)에 대한 제출 정보 조회
    * 
    * @param assignment 과제 객체
    * @return 해당 과제에 제출된 목록
    */

     boolean existsByExamIdAndMember_StudentId(Long examId, String studentId);

     List<Submission> findByExamId(Long examId);
     
     Optional<Submission> findByMemberId(Long memberId);  
     Optional<Submission> findByMemberIdAndExamId(Long memberId, Long examId);

     
}
