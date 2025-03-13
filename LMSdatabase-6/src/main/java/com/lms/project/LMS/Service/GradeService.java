package com.lms.project.LMS.Service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.Grade;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.GradeRepository;
import com.lms.project.LMS.Repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * 성적(Grade) 관리를 위한 서비스 클래스 - 성적 조회, 입력 기능 제공
 */
@Service
public class GradeService {

	private final GradeRepository gradeRepository;
	private final MemberRepository memberRepository;

	public GradeService(GradeRepository gradeRepository, MemberRepository memberRepository) {
		this.gradeRepository = gradeRepository;
		this.memberRepository = memberRepository;
	}

	/**
	 * 모든 성적 정보 조회
	 * 
	 * @return 전체 성적 목록
	 */
	public List<Grade> getAllGrades() {
		return gradeRepository.findAll(); // 수정된 메서드명 적용
	}

	/**
	 * 특정 학생의 성적 조회
	 * 
	 * @param studentId 학생 ID
	 * @return 해당 학생의 성적 목록
	 */
	public List<Grade> getGradesByStudentId(Long studentId) {
		return gradeRepository.findByStudent_Id(studentId); // 🔥 수정된 부분 적용
	}

	/**
	 * 특정 교수의 성적 조회 (필요할 경우)
	 * 
	 * @param professorId 교수 ID
	 * @return 해당 교수가 입력한 성적 목록
	 */
	public List<Grade> getGradesByProfessorId(Long professorId) {
		return gradeRepository.findByProfessor_Id(professorId); // 🔥 추가된 기능
	}

	/**
	 * 성적 입력 (교수만 가능)
	 * 
	 * @param professorId 교수 ID
	 * @param grade       입력할 성적 객체
	 * @return 저장된 성적 객체
	 */
	public Grade assignGrade(Long professorId, Grade grade) {
		Member professor = memberRepository.findById(professorId)
				.orElseThrow(() -> new EntityNotFoundException("Professor not found"));

		// 역할 검증: 교수만 성적 입력 가능
		// 수정된 부분: 문자열로 비교
		if (!professor.getRole().equals("PROFESSOR")) { // 수정된 부분: 문자열로 비교
			throw new IllegalArgumentException("Only professors can assign grades.");
		}

		grade.setProfessor(professor); // 교수 정보 설정
		return gradeRepository.save(grade);
	}

	/**
	 * 성적 변환 (점수를 A, B, C, D, F 학점으로 변환)
	 * 
	 * @param score 성적 점수
	 * @return 변환된 학점
	 */
	public String convertScoreToGrade(Double score) {
		if (score >= 90)
			return "A";
		if (score >= 80)
			return "B";
		if (score >= 70)
			return "C";
		if (score >= 60)
			return "D";
		return "F";
	}

	/**
	 * 성적 분포도 다운로드 (엑셀 파일로 내보내기)
	 */
	public void downloadGradeDistribution() {
		// TODO: 엑셀 생성 및 다운로드 기능 추가 (Apache POI 라이브러리 사용 가능)
		System.out.println("엑셀 다운로드 기능 구현 중...");
	}
}
