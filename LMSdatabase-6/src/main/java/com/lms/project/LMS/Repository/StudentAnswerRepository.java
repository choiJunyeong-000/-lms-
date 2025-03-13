package com.lms.project.LMS.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Entity.StudentAnswer;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByExamId(Long examId);

    Optional<StudentAnswer> findByStudentIdAndQuestionId(Long studentId, Long questionId);
    List<StudentAnswer> findByStudentId(Long studentId);
    Optional<StudentAnswer> findByStudent_StudentIdAndQuestion_Id(String studentId, Long questionId);

   List<StudentAnswer> findByExamIdAndQuestionIn(Long examId, List<ExamQuestion> examQuestions);
    
}