package com.lms.project.LMS.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // <- "/api" 경로를 포함해야 함
public class RestApiController {

	@GetMapping("/loginSuccess")
	@PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
	public ResponseEntity<Map<String, String>> loginSuccess(@RequestHeader(value = "Authorization", required = false) String token) {
	    System.out.println("🛠 loginSuccess 엔드포인트 요청 도착");

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("message", "로그인이 필요합니다."));
	    }

	    Optional<String> userRole = authentication.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .findFirst();

	    System.out.println("✅ 로그인 성공 - 사용자 역할: " + userRole.orElse("ROLE_UNKNOWN"));

	    return ResponseEntity.ok(Map.of("userRole", userRole.orElse("ROLE_USER")));
	}
}