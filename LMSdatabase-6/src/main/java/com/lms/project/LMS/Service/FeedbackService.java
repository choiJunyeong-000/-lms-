package com.lms.project.LMS.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.Feedback;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.FeedbackRepository;
import com.lms.project.LMS.Repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FeedbackService {

	private final FeedbackRepository feedbackRepository;
	private final MemberRepository memberRepository;

	public FeedbackService(FeedbackRepository feedbackRepository, MemberRepository memberRepository) {
		this.feedbackRepository = feedbackRepository;
		this.memberRepository = memberRepository;
	}

	// 📌 피드백 저장 (관리자 또는 특정 사용자가 직접 저장할 때)
	public void saveFeedback(Feedback feedback) {
		feedbackRepository.save(feedback);
	}

	// 📌 특정 강의의 피드백 조회
	public List<Feedback> getFeedbackByCourseId(Long courseId) {
		return feedbackRepository.findByCourseId(courseId);
	}

	// 📌 피드백 작성 (학생만 가능)
	public Feedback submitFeedback(Long memberId, Feedback feedback) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 수정된 부분: 역할을 문자열로 비교
		if (!member.getRole().equals("STUDENT")) { // 수정된 부분: 문자열로 비교
			throw new IllegalArgumentException("Only students can submit feedback.");
		}

		feedback.setMember(member);
		return feedbackRepository.save(feedback);
	}
}
