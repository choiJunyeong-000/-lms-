package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.Entity.CourseQnA;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.QnAAnswer;

import java.util.List;

@Repository
public interface QnAAnswerRepository extends JpaRepository<QnAAnswer, Long> {
    List<QnAAnswer> findByCourseQnA(CourseQnA courseQnA);
    List<QnAAnswer> findAllByCourseQnAId(Long qnaId);  
    
    @Transactional
    void deleteAllByCourseQnA(CourseQnA courseQnA); // CourseQnA 관련된 모든 답변 삭제
    
    List<QnAAnswer> findByAuthor(Member author); // ✅ 특정 멤버가 작성한 답변 조회
    
  

}
