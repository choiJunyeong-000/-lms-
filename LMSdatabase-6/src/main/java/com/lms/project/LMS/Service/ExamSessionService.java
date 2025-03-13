package com.lms.project.LMS.Service; // 네 패키지 적용

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.ExamSession;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.ExamRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.ExamSessionRepository;

/**
 * 시험 세션(ExamSession)을 관리하는 서비스 클래스 - 시험 시작 및 종료 기능 제공
 */
@Service
public class ExamSessionService {

	private final ExamRepository examRepository;
	private final MemberRepository memberRepository;
	private final ExamSessionRepository examSessionRepository;

	@Autowired
	public ExamSessionService(ExamRepository examRepository, MemberRepository memberRepository,
			ExamSessionRepository examSessionRepository) {
		this.examRepository = examRepository;
		this.memberRepository = memberRepository;
		this.examSessionRepository = examSessionRepository;
	}

	/**
	 * 시험 세션을 시작하는 메서드
	 * 
	 * @param examId    시험 ID
	 * @param studentId 학생 ID
	 * @return 생성된 시험 세션 객체
	 */
	public ExamSession startExam(Long examId, Long studentId) {
		Exam exam = examRepository.findById(examId).orElseThrow(() -> new IllegalArgumentException("시험이 존재하지 않습니다."));
		Member student = memberRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));

		ExamSession session = new ExamSession();
		session.setExam(exam);
		session.setMember(student);
		session.setStartTime(LocalDateTime.now());
		session.setCheatingDetected(false);

		return examSessionRepository.save(session); // DB에 저장
	}

	/**
	 * 시험 세션을 종료하는 메서드
	 * 
	 * @param sessionId 종료할 시험 세션 ID
	 */
	public void endExam(Long sessionId) {
		ExamSession session = examSessionRepository.findById(sessionId)
				.orElseThrow(() -> new IllegalArgumentException("시험 세션이 존재하지 않습니다."));
		session.setEndTime(LocalDateTime.now());
		examSessionRepository.save(session); // 변경 사항 저장
	}
}
