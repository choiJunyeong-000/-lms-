package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Repository.SubmissionRepository;
import com.lms.project.LMS.Repository.ExamRepository; // 🔹 추가
import com.lms.project.LMS.Repository.AssignmentRepository; // 🔹 추가
import com.lms.project.LMS.Repository.MemberRepository; // 🔹 추가
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 과제 제출 관리 서비스 클래스
 */
@Service
public class SubmissionService {

	private final SubmissionRepository submissionRepository;
	private final ExamRepository examRepository; // 🔹 추가
	private final AssignmentRepository assignmentRepository; // 🔹 추가
	private final MemberRepository memberRepository; // 🔹 추가

	// 생성자에 의존성 주입 추가
	public SubmissionService(SubmissionRepository submissionRepository, ExamRepository examRepository, // 🔹 추가
			AssignmentRepository assignmentRepository, // 🔹 추가
			MemberRepository memberRepository // 🔹 추가
	) {
		this.submissionRepository = submissionRepository;
		this.examRepository = examRepository; // 🔹 추가
		this.assignmentRepository = assignmentRepository; // 🔹 추가
		this.memberRepository = memberRepository; // 🔹 추가
	}

	/**
	 * 모든 제출 데이터 조회
	 * 
	 * @return 전체 제출 목록
	 */
	public List<Submission> getAllSubmissions() {
		return submissionRepository.findAll();
	}

	/**
	 * 특정 시험에 대한 제출 정보 조회
	 * 
	 * @param examId 시험 ID
	 * @return 해당 시험의 제출 목록
	 */
	public List<Submission> getSubmissionsByExamId(Long examId) {
		Exam exam = examRepository.findById(examId).orElseThrow(() -> new IllegalArgumentException("시험이 존재하지 않습니다."));
		return submissionRepository.findByExam(exam);
	}

	/**
	 * 특정 과제의 제출 정보 조회
	 * 
	 * @param assignmentId 과제 ID
	 * @return 해당 과제의 제출 목록
	 */
	

	/**
	 * 특정 학생의 제출 정보 조회
	 * 
	 * @param memberId 학생 ID
	 * @return 해당 학생의 제출 목록
	 */
	public List<Submission> getSubmissionsByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));
		return submissionRepository.findByMember(member);
	}

	/**
	 * 제출 정보 저장
	 * 
	 * @param submission 제출 데이터
	 * @return 저장된 제출 정보
	 */
	 // 제출 상태 확인 메서드 추가
	 public boolean isSubmitted(Long examId, String studentId) {
	        return submissionRepository.existsByExamIdAndMember_StudentId(examId, studentId);
	    }
}
