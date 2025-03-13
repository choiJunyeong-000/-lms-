package com.lms.project.LMS.controller;

import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat-members")
public class ChatMemberController {

    private final MemberRepository memberRepository;

    @Autowired
    public ChatMemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // ✅ 학생과 교수만 반환하는 API
    @GetMapping
    public List<Member> getChatMembers() {
        return memberRepository.findAll().stream()
                .filter(member -> "STUDENT".equals(member.getRole()) || "PROFESSOR".equals(member.getRole()))
                .collect(Collectors.toList());
    }
}
