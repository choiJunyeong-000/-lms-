package com.lms.project.LMS.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 기본 홈 페이지
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 회원가입 페이지
    @GetMapping("/signUp")
    public String loadSignUp() {
        return "member/signUp";
    }

    // 로그인 페이지
    @GetMapping("/loginHome")
    public String loginHome() {
        return "member/loginHome";
    }

    // 로그인 성공 페이지
    @GetMapping("/loginSuccess")
    public String loginSuccess(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/loginHome"; // 인증 정보가 없으면 로그인 페이지로 리다이렉트
        }

        String userRole = authentication.getAuthorities().stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .findFirst()
                                         .orElse("ROLE_USER");

        model.addAttribute("userRole", userRole);
        return "loginSuccess";
    }


    // 학생 전용 페이지
    @GetMapping("/studentPage")
    public String studentPage() {
        return "member/studentPage";
    }

    // 교수 전용 페이지
    @GetMapping("/professorPage")
    public String professorPage() {
        return "member/professorPage";
    }

    // 관리자 전용 페이지
    @GetMapping("/adminPage")
    public String adminPage() {
        return "member/adminPage";
    }
    
 // 유저 페이지
    @GetMapping("/main")
    public String userPage() {
        return "/main"; // 템플릿 파일 이름
    }
}
