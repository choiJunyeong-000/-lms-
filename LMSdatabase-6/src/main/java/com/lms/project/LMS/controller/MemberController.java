package com.lms.project.LMS.controller;


import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/users")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public Member createUser(@RequestBody Member user) {
        return memberService.saveUser(user);
    }
 // ✅ 특정 회원 정보 조회 (GET 요청 추가)
    @GetMapping("/{id}")
    public ResponseEntity<Member> getUserById(@PathVariable Long id) {
        Member user = memberService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<Member> getAllUsers() {
        return memberService.findAllUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Member user, HttpServletResponse response) {
        try {
            // 로그인 후 JWT 토큰 생성
            String token = memberService.login(user.getStudentId(), user.getPassword());

            // JWT를 쿠키에 저장
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(false);  // JavaScript에서 쿠키를 접근할 수 없도록 설정
            cookie.setSecure(false);   // 로컬 환경에서는 false, 배포 환경에서는 true (HTTPS 필요)
            cookie.setPath("/");       // 모든 경로에서 사용할 수 있도록 설정
            cookie.setMaxAge(60 * 60); // 1시간 동안 쿠키 유지
            cookie.setAttribute("SameSite", "Lax"); // 다른 포트에서도 사용할 수 있도록 설정

            response.addCookie(cookie);  // 쿠키를 HTTP 응답에 추가
            
            // 클라이언트로 JWT 토큰을 직접 반환하지 않음
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateUser(@PathVariable Long id, @RequestBody Member updatedUser) {
        Member user = memberService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }
 // ✅ 현재 로그인한 회원 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<Member> getCurrentUser(Principal principal) {
        String studentId = principal.getName(); // ✅ 현재 로그인한 사용자의 학번(studentId) 가져오기
        Member user = memberService.findUserByStudentId(studentId);
        return ResponseEntity.ok(user);  // ✅ 정상적으로 사용자 정보 반환
    }


    // ✅ 현재 로그인한 회원이 자기 정보를 수정
    @PutMapping("/update")
    public ResponseEntity<Member> updateUser(Principal principal, @RequestBody Member updatedUser) {
        String studentId = principal.getName(); // 현재 로그인한 사용자의 학번(studentId) 가져오기
        Member user = memberService.updateUserByStudentId(studentId, updatedUser);
        return ResponseEntity.ok(user);
    }
    // 이메일 중복 여부 확인
    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestBody String email) {
        boolean exists = memberService.checkEmailExists(email);
        return ResponseEntity.ok(exists);
    }
 // ✅ 사용자 삭제 엔드포인트 추가
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        memberService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
