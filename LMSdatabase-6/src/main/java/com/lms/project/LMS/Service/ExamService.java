package com.lms.project.LMS.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Exam;
import com.lms.project.LMS.Entity.ExamQuestion;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.ExamRepository;
import com.lms.project.LMS.Repository.ExamQuestionRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.CourseRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ExamService {

	private final ExamRepository examRepository;
	private final ExamQuestionRepository questionRepository;
	private final MemberRepository memberRepository;
	private final CourseRepository courseRepository;

	public ExamService(ExamRepository examRepository, ExamQuestionRepository questionRepository,
			MemberRepository memberRepository, CourseRepository courseRepository) {
		this.examRepository = examRepository;
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.courseRepository = courseRepository;
	}

	// ✅ 시험 생성 (교수만 가능)
	@Transactional
	public Exam createExam(Long memberId, Long courseId, Exam exam) {
		if (memberId == null) {
			throw new IllegalArgumentException("Member ID cannot be null");
		}

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 역할이 교수인지 확인 (대소문자 구분 없이)
		if (!"PROFESSOR".equalsIgnoreCase(member.getRole())) { 
			throw new IllegalArgumentException("Only professors can create exams.");
		}
		
		Course course = courseRepository.findById(courseId)
	            .orElseThrow(() -> new EntityNotFoundException("Course not found"));

		// 시험과 교수 연결
		exam.setMember(member);
		exam.setCourse(course);
		return examRepository.save(exam);
	}

	// ✅ 문제 추가
	@Transactional
	public void addQuestion(Long examId, ExamQuestion question) {
		Exam exam = examRepository.findById(examId).orElseThrow(() -> new EntityNotFoundException("Exam not found"));

		question.setExam(exam);
		questionRepository.save(question);

		exam.addQuestion(question);
		examRepository.save(exam);
	}

	// ✅ 모든 시험 조회
	public List<Exam> getAllExams() {
		return examRepository.findAll();
	}

	// ✅ 특정 시험 조회
	public Exam getExamById(Long examId) {
		return examRepository.findById(examId).orElseThrow(() -> new EntityNotFoundException("Exam not found"));
	}

	// ✅ 특정 강의의 시험 조회
	public List<Exam> getExamsByCourseId(Long courseId) {
		return examRepository.findByCourseId(courseId);
	}

	// ✅ 특정 교수의 시험 조회 (프론트엔드에서 필요)
	public List<Exam> getExamsByProfessorId(Long professorId) {
		Member professor = memberRepository.findById(professorId)
				.orElseThrow(() -> new EntityNotFoundException("Professor not found"));

		if (!"PROFESSOR".equalsIgnoreCase(professor.getRole())) {
			throw new IllegalArgumentException("Member is not a professor.");
		}

		return examRepository.findByMember(professor);
	}
}
