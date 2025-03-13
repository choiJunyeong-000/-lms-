package com.lms.project.LMS.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Enum.ExamQuestionType;
import com.lms.project.LMS.Repository.ExamQuestionRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.SubmissionRepository;

import com.lms.project.LMS.Entity.StudentAnswer;
import com.lms.project.LMS.Repository.StudentAnswerRepository;
import java.util.Optional;

@Service
public class GradingService {
    
    private final ExamQuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;
    private final StudentAnswerRepository studentAnswerRepository; // 🔹 추가됨
    private final MemberRepository memberRepository;

    @Autowired
    public GradingService(ExamQuestionRepository questionRepository, 
                          SubmissionRepository submissionRepository,
                          StudentAnswerRepository studentAnswerRepository,
                          MemberRepository memberRepository) {
        this.questionRepository = questionRepository;
        this.submissionRepository = submissionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.memberRepository=memberRepository;// 🔹 추가됨
    }

    /**
     * 제출된 답안을 기반으로 시험 자동 채점
     * 
     * @param submission 학생 제출 정보
     * @return 채점된 점수
     */
    public void gradeExam(Long studentId, Long examId, Map<Long, String> answers) {
        if (examId == null) {
            throw new IllegalArgumentException("Exam ID cannot be null");
        }

        List<ExamQuestion> questions = questionRepository.findByExamId(examId);
        int totalScore = 0;

        for (ExamQuestion question : questions) {
            String studentAnswer = answers.get(question.getId());
            int questionScore = 0;

            if (question.getType() == ExamQuestionType.MULTIPLE_CHOICE) {
                if (studentAnswer != null && studentAnswer.equals(question.getCorrectAnswer())) {
                    questionScore = question.getScore(); // 정답이면 점수 할당
                }
            }


            Optional<StudentAnswer> studentAnswerOpt = studentAnswerRepository.findByStudent_StudentIdAndQuestion_Id(String.valueOf(studentId), question.getId());

            if (studentAnswerOpt.isPresent()) {
                StudentAnswer studentAnswerEntity = studentAnswerOpt.get();

                // 1️⃣ 기존 점수와 비교하여 변경이 있으면 저장
                if (!studentAnswerEntity.getScore().equals(questionScore)) {
                    studentAnswerEntity.setScore(questionScore);
                    studentAnswerRepository.save(studentAnswerEntity); // 변경 사항 저장
                } else {
                }
            } else {
                System.out.println("⚠ 기존 StudentAnswer를 찾을 수 없음! 해당 문제에 대한 응답이 저장되지 않았을 수 있음.");
            }
            totalScore += questionScore;

        }



        

        // 2️⃣ studentId로 Member를 조회하여 해당 Member의 id를 사용해 Submission 찾기
        Optional<Member> memberOpt = memberRepository.findByStudentId(String.valueOf(studentId));
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            // member.id와 examId를 사용하여 Submission 조회
            Optional<Submission> submissionOpt = submissionRepository.findByMemberIdAndExamId(member.getId(), examId);
            if (submissionOpt.isPresent()) {
                Submission submissionEntity = submissionOpt.get();
                submissionEntity.setTotalScore(totalScore);
                submissionRepository.save(submissionEntity);
            } else {
                System.out.println("⚠ Submission을 찾을 수 없음! 총점 업데이트 실패.");
                
            }
        } else {
            System.out.println("⚠ Member를 찾을 수 없음! 총점 업데이트 실패.");
        }
    }


    public void saveEssayScores(Submission submission) {
        submissionRepository.save(submission);
    }
}