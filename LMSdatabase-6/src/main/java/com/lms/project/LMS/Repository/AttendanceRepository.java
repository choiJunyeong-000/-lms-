package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Attendance;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Video;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // studentId를 String으로 변경
    List<Attendance> findByMember_StudentId(String studentId);
    // studentId와 courseId로 출석 정보 조회하는 메서드 추가
    List<Attendance> findByMember_StudentIdAndCourse_Id(String studentId, Long courseId);

    Optional<Attendance> findByMemberAndCourse(Member member, Course course);

    Optional<Attendance> findByMemberAndVideo(Member member, Video video);

    List<Attendance> findByCourseId(Long courseId);
    
 // studentId와 videoId로 출석 정보 조회하는 메서드 추가
    Optional<Attendance> findByMember_StudentIdAndVideo_Id(String studentId, Long videoId);
    
    List<Attendance> findByVideoId(Long videoId);
    Optional<Attendance> findByMemberAndContent(Member member, Content content);  // Content별 출석 정보 조회
    
  
 // ✅ member_id를 기준으로 삭제하는 메서드 추가
    @Transactional
    void deleteByMemberId(Long memberId);
    
 // ✅ 특정 학생(member_id)과 강의(content_id)에 대한 출석 정보 리스트 조회
    @Query("SELECT a FROM Attendance a WHERE a.member.id = :memberId AND a.content.id = :contentId")
    List<Attendance> findByMemberIdAndContentId(Long memberId, Long contentId);
}
