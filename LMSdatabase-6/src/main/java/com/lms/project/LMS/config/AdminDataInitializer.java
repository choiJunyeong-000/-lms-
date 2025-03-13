package com.lms.project.LMS.config;


import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner initAdminUser(MemberRepository userRepository, PasswordEncoder passwordEncoder) { // ✅ PasswordEncoder 주입
        return args -> {
            if (userRepository.findByStudentId("admin").isEmpty()) {
                Member admin = new Member();
                admin.setName("관리자");
                admin.setStudentId("admin");
                admin.setPassword(passwordEncoder.encode("1234")); // ✅ 비밀번호 암호화 적용
                admin.setRole("ROLE_ADMIN");
                admin.setBirthDate("1990-01-01");
                admin.setEmail("admin@system.com");
                userRepository.save(admin);
                System.out.println("✅ 관리자 계정이 등록되었습니다.");
            }
        };
    }
}
