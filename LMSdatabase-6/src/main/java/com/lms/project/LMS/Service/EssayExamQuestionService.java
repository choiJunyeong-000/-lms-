package com.lms.project.LMS.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.project.LMS.DTO.EssayExamQuestionDTO;
import com.lms.project.LMS.Entity.EssayExamQuestion;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Enum.ExamQuestionType;
import com.lms.project.LMS.Repository.EssayExamQuestionRepository;
import com.lms.project.LMS.Repository.ExamQuestionRepository;
import com.lms.project.LMS.Repository.ExamRepository;

import java.util.List;

@Service
public class EssayExamQuestionService {

    private final EssayExamQuestionRepository essayExamQuestionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamRepository examRepository;

    @Autowired
    public EssayExamQuestionService(EssayExamQuestionRepository essayExamQuestionRepository,
                                     ExamQuestionRepository examQuestionRepository, ExamRepository examRepository) {
        this.essayExamQuestionRepository = essayExamQuestionRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examRepository = examRepository;
    }

    // 서술형 문제 답변 저장
    public void addEssayAnswer(EssayExamQuestion essayExamQuestion) {
        if (essayExamQuestion == null) {
            throw new IllegalArgumentException("서술형 문제 답변이 null입니다.");
        }
        essayExamQuestionRepository.save(essayExamQuestion);
    }

    // 특정 시험의 서술형 문제 생성 (중복 방지 추가)
    public void createEssayQuestionsFromExamId(Long examId) {
        List<ExamQuestion> questions = examQuestionRepository.findByExamId(examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시험이 존재하지 않습니다."));

        for (ExamQuestion question : questions) {
            if (question.getType() == ExamQuestionType.ESSAY) {
                if (essayExamQuestionRepository.findByExamQuestionId(question.getId()).isEmpty()) {
                    EssayExamQuestion essayExamQuestion = new EssayExamQuestion();
                    essayExamQuestion.setExamQuestion(question);
                    essayExamQuestion.setStudentAnswer(""); // 초기 답변
                    essayExamQuestion.setScore(0); // 초기 점수
                    essayExamQuestion.setQuestionText(question.getQuestionText());
                    essayExamQuestion.setExam(exam);

                    essayExamQuestionRepository.save(essayExamQuestion);
                }
            }
        }
    }

    // 특정 서술형 문제 조회
    public EssayExamQuestionDTO getEssayQuestionByQuestionId(Long questionId) {
        EssayExamQuestion essayExamQuestion = essayExamQuestionRepository.findByExamQuestionId(questionId)
                .orElseThrow(() -> new IllegalArgumentException("서술형 질문이 존재하지 않습니다."));
        return convertToDTO(essayExamQuestion);
    }

    // 서술형 문제 점수 업데이트
    public void updateScore(Long essayQuestionId, Integer score) {
        EssayExamQuestion essayExamQuestion = essayExamQuestionRepository.findById(essayQuestionId)
                .orElseThrow(() -> new IllegalArgumentException("서술형 질문이 존재하지 않습니다."));
        essayExamQuestion.setScore(score);
        essayExamQuestionRepository.save(essayExamQuestion);
    }

    // 서술형 답변 업데이트
    public void updateEssayAnswer(Long essayQuestionId, String studentAnswer) {
        EssayExamQuestion essayExamQuestion = essayExamQuestionRepository.findById(essayQuestionId)
                .orElseThrow(() -> new IllegalArgumentException("서술형 질문이 존재하지 않습니다."));
        essayExamQuestion.setStudentAnswer(studentAnswer);
        essayExamQuestionRepository.save(essayExamQuestion);
    }

    // 서술형 문제 Entity → DTO 변환
    private EssayExamQuestionDTO convertToDTO(EssayExamQuestion essayExamQuestion) {
        EssayExamQuestionDTO dto = new EssayExamQuestionDTO();
        dto.setId(essayExamQuestion.getId());
        dto.setExamQuestionId(essayExamQuestion.getExamQuestion().getId());
        dto.setQuestionText(essayExamQuestion.getQuestionText());
        dto.setStudentAnswer(essayExamQuestion.getStudentAnswer());
        dto.setScore(essayExamQuestion.getScore());
        return dto;
    }
}
