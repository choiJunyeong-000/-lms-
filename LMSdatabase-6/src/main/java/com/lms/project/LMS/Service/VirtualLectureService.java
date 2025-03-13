package com.lms.project.LMS.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.VirtualLecture;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.VirtualLectureRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * 가상 강의 관리 서비스 클래스
 */
@Service
public class VirtualLectureService {

	private final VirtualLectureRepository virtualLectureRepository;
	private final MemberRepository memberRepository;

	public VirtualLectureService(VirtualLectureRepository virtualLectureRepository, MemberRepository memberRepository) {
		this.virtualLectureRepository = virtualLectureRepository;
		this.memberRepository = memberRepository;
	}

	// 가상 강의 생성 (교수만 가능)
	public VirtualLecture createVirtualLecture(Long memberId, VirtualLecture virtualLecture) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 역할 검증: 교수만 가상 강의 생성 가능
		if (!"PROFESSOR".equals(member.getRole())) {
			throw new IllegalArgumentException("Only professors can create virtual lectures.");
		}

		virtualLecture.setMember(member); // 교수 설정
		return virtualLectureRepository.save(virtualLecture);
	}

	// 특정 강의의 가상 강의 조회
	public List<VirtualLecture> getVirtualLecturesByCourseId(Long courseId) {
		return virtualLectureRepository.findByCourseId(courseId);
	}

	// 특정 교수가 생성한 가상 강의 조회
	public List<VirtualLecture> getVirtualLecturesByProfessorId(Long memberId) {
		return virtualLectureRepository.findByMemberId(memberId);
	}
}
