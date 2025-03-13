package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ Spring Data JPA 기반 Member Repository - 학생 ID, 이메일, 이름으로 멤버 조회 - 역할(Role)로
 * 멤버 목록 조회 - ID로 멤버 조회
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	// ✅ 학생 ID로 멤버 조회 (로그인용)
	Optional<Member> findByStudentId(String studentId);

	// ✅ 학생 ID와 비밀번호로 멤버 조회 (로그인 검증용)
	Optional<Member> findByStudentIdAndPassword(String studentId, String password);

	// ✅ 이메일로 멤버 조회
	Optional<Member> findByEmail(String email);

	// ❌ username 관련 메서드 제거 완료! (이제 studentId 사용)

	// ✅ 역할(Role) 기반 회원 조회
	List<Member> findByRole(String role);

	// ✅ 이름으로 멤버 조회
	Optional<Member> findByName(String name);

	// ✅ ID로 멤버 조회
	Optional<Member> findById(Long id);

	// ✅ 이메일이 이미 존재하는지 확인하는 메서드 추가
	boolean existsByEmail(String email);
	
	Optional<Member> findByname(String name);  // `name`으로 회원 찾기
	
	
}
