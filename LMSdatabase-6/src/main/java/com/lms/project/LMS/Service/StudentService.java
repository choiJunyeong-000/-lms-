package com.lms.project.LMS.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * ✅ 학생(Member Role: STUDENT) 관련 서비스 클래스
 */
@Service
public class StudentService {

    private final MemberRepository memberRepository;

    public StudentService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * ✅ ID로 학생 조회 (팀원 코드 유지)
     */
    public Optional<Member> findById(Long studentId) {
        // role이 "STUDENT"인지 확인하도록 수정
        return memberRepository.findById(studentId).filter(member -> "STUDENT".equals(member.getRole()));
    }

    /**
     * ✅ 모든 학생 조회
     */
    public List<Member> getAllStudents() {
        // role이 "STUDENT"인 멤버만 조회하도록 수정
        return memberRepository.findByRole("STUDENT");
    }

    /**
     * ✅ ID별 학생 조회
     */
    public Member findStudentById(Long id) {
        // role이 "STUDENT"인지 확인하고 학생을 찾도록 수정
        return memberRepository.findById(id)
                .filter(member -> "STUDENT".equals(member.getRole()))
                .orElseThrow(() -> new EntityNotFoundException("❌ Student not found"));
    }

    /**
     * ✅ 학생 생성
     */
    public Member createStudent(Member student) {
        // 학생의 role을 "STUDENT"로 설정하고 저장
        student.setRole("STUDENT");
        return memberRepository.save(student);
    }

    /**
     * ✅ 학생 삭제
     */
    @Transactional
    public void deleteStudent(Long id) {
        // 학생을 ID로 찾고 삭제
        Member student = findStudentById(id);
        memberRepository.delete(student);
    }
}
