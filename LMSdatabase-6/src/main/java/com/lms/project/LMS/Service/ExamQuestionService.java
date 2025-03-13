package com.lms.project.LMS.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lms.project.LMS.Entity.EssayExamQuestion;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.StudentAnswer;
import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Enum.ExamQuestionType;
import com.lms.project.LMS.Repository.EssayExamQuestionRepository;
import com.lms.project.LMS.Repository.ExamQuestionRepository;
import com.lms.project.LMS.Repository.ExamRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.StudentAnswerRepository;
import com.lms.project.LMS.Repository.SubmissionRepository;

/**
 * 시험 문제(ExamQuestion) 관리를 위한 서비스 클래스 - 시험 문제 추가, 조회, 서술형 문제 관리 기능 제공
 */
@Service
public class ExamQuestionService {

    private final ExamQuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final EssayExamQuestionRepository essayExamQuestionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final SubmissionRepository submissionRepository;
    private final MemberRepository memberRepository;


    @Autowired
    public ExamQuestionService(ExamQuestionRepository questionRepository, ExamRepository examRepository,
                               EssayExamQuestionRepository essayExamQuestionRepository, StudentAnswerRepository studentAnswerRepository, SubmissionRepository submissionRepository,
                               MemberRepository memberRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.essayExamQuestionRepository = essayExamQuestionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.submissionRepository = submissionRepository;
        this.memberRepository = memberRepository;
    }

	/**
	 * 특정 시험에 문제 추가 - 서술형 문제일 경우 `EssayExamQuestion`도 자동 생성
	 * 
	 * @param examId   시험 ID
	 * @param question 추가할 문제
	 */
	@Transactional
	public void addQuestion(Long examId, ExamQuestion question) {
		Exam exam = examRepository.findById(examId).orElseThrow(() -> new IllegalArgumentException("시험이 존재하지 않습니다."));

		question.setExam(exam);
		questionRepository.save(question);

		// 서술형 문제일 경우 EssayExamQuestion도 생성 (중복 방지)
		if (question.getType() == ExamQuestionType.ESSAY) {
			boolean exists = essayExamQuestionRepository.findByExamQuestionId(question.getId()).isPresent();
			if (!exists) {
				EssayExamQuestion essayExamQuestion = new EssayExamQuestion();
				essayExamQuestion.setExamQuestion(question);
				essayExamQuestion.setQuestionText(question.getQuestionText());
				essayExamQuestion.setScore(0);
				essayExamQuestion.setStudentAnswer("");
				essayExamQuestion.setExam(exam);

				essayExamQuestionRepository.save(essayExamQuestion);
			}
		}

		exam.addQuestion(question);
		examRepository.save(exam);
	}

	/**
	 * 특정 시험 ID에 대한 문제 목록 조회
	 * 
	 * @param examId 시험 ID
	 * @return 해당 시험에 속한 문제 리스트
	 */
	public List<ExamQuestion> getQuestionsByExamId(Long examId) {
		Exam exam = examRepository.findById(examId).orElseThrow(() -> new IllegalArgumentException("시험이 존재하지 않습니다."));
		return exam.getQuestions();
	}

	/**
	 * 특정 시험 ID에 대한 객관식 문제 목록 조회
	 * 
	 * @param examId 시험 ID
	 * @return 해당 시험에서 객관식 문제만 조회
	 */
	public List<ExamQuestion> getMultipleChoiceQuestionsByExamId(Long examId) {
		return questionRepository.findByExamIdAndType(examId, ExamQuestionType.MULTIPLE_CHOICE);
	}

	/**
	 * 특정 시험 ID에 대한 서술형 문제 목록 조회
	 * 
	 * @param examId 시험 ID
	 * @return 해당 시험에서 서술형 문제만 조회
	 */
	public List<ExamQuestion> getEssayQuestionsByExamId(Long examId) {
		return questionRepository.findByExamIdAndType(examId, ExamQuestionType.ESSAY);
	}

	/**
	 * 특정 시험 ID의 서술형 문제를 `EssayExamQuestion` 테이블로 이동 (중복 방지 추가)
	 * 
	 * @param examId 시험 ID
	 */
	@Transactional
	public void moveEssayQuestionsToEssayExamQuestions(Long examId) {
		List<ExamQuestion> essayQuestions = questionRepository.findByExamIdAndType(examId, ExamQuestionType.ESSAY);
		Exam exam = examRepository.findById(examId).orElseThrow(() -> new IllegalArgumentException("시험이 존재하지 않습니다."));

		for (ExamQuestion question : essayQuestions) {
			boolean exists = essayExamQuestionRepository.findByExamQuestionId(question.getId()).isPresent();
			if (!exists) {
				EssayExamQuestion essayExamQuestion = new EssayExamQuestion();
				essayExamQuestion.setExamQuestion(question);
				essayExamQuestion.setQuestionText(question.getQuestionText());
				essayExamQuestion.setScore(0);
				essayExamQuestion.setStudentAnswer("");
				essayExamQuestion.setExam(exam);

				essayExamQuestionRepository.save(essayExamQuestion);
			}
		}
	}
	
	/**
     * ✅ 문제 수정 기능 추가 (제목, 점수, 답변 수정 가능)
     */
    @Transactional
    public boolean updateQuestion(Long examId, Long questionId, ExamQuestion updatedQuestion) {
        Optional<ExamQuestion> optionalQuestion = questionRepository.findById(questionId);

        if (optionalQuestion.isPresent()) {
            ExamQuestion existingQuestion = optionalQuestion.get();

            // 문제 수정
            existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
            existingQuestion.setScore(updatedQuestion.getScore());
            existingQuestion.setType(updatedQuestion.getType());
            existingQuestion.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
            existingQuestion.setAnswers(updatedQuestion.getAnswers());

            // 저장
            questionRepository.save(existingQuestion);
            return true;
        }
        return false;
    }
    
    @Transactional
    public void saveAnswers(Long examId, String studentId, Map<String, String> answers) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("시험이 존재하지 않습니다."));

        Member student = memberRepository.findByStudentId(studentId)  // studentId로 조회
            .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));

        // String 키를 Long 키로 변환
        Map<Long, String> convertedAnswers = answers.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> Long.parseLong(entry.getKey()),
                Map.Entry::getValue
            ));

        // 답변 저장 로직
        convertedAnswers.forEach((questionId, answer) -> {
            ExamQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("문제가 존재하지 않습니다."));

            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setExam(exam);
            studentAnswer.setQuestion(question);
            studentAnswer.setStudent(student);  // Member 엔티티로 설정
            studentAnswer.setAnswer(answer);

            studentAnswerRepository.save(studentAnswer);
        });

        // 제출 상태 저장
        Submission submission = new Submission();
        submission.setExam(exam);
        submission.setMember(student);  // 수정된 부분
        submission.setAnswers(convertedAnswers);
        submissionRepository.save(submission);
    }

    // 제출 상태 확인 메서드 추가
    public boolean isSubmitted(Long examId, String studentId) {
        return submissionRepository.existsByExamIdAndMember_StudentId(examId, studentId);
    }
}