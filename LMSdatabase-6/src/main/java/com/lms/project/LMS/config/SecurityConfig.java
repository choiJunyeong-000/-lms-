package com.lms.project.LMS.config;

import com.lms.project.LMS.constants.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ JWT 사용 시 Stateless 설정
            .and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers("/uploads/**").permitAll() // 파일 다운로드는 인증 없이 허용
                .requestMatchers("/api/courses/upload-video").permitAll() // 파일 다운로드는 인증 없이 허용
                .requestMatchers("/api/users/update").authenticated()
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/metrics-view").authenticated()
                .requestMatchers("/api/users/check-email").authenticated()
                .requestMatchers("/api/attendance").authenticated()
                .requestMatchers("/contents").authenticated()
               // .requestMatchers("/courses").authenticated()
                .requestMatchers("/api/essay-questions").authenticated()
                .requestMatchers("/api/evaluations").authenticated()
                .requestMatchers("/api/exams/**").authenticated()
                .requestMatchers("/api/questions").authenticated()
                .requestMatchers("/api/grades").authenticated()
                .requestMatchers("/api/grade").authenticated()
                .requestMatchers("/api/students").authenticated()  
                .requestMatchers("/courses/**").permitAll()
                .requestMatchers("/api/student-manage/**").permitAll()
                .requestMatchers("/weekly-board/**").permitAll()
                .requestMatchers("weekly-post/**").permitAll()
                .requestMatchers("/api/files/**").permitAll()
                .requestMatchers("/api/courses/download").authenticated()  // 다운로드는 인증 필요
                .requestMatchers("/api/chat-members/**").authenticated()
                .requestMatchers("/api/courses/**").hasAnyAuthority("PROFESSOR","ROLE_ADMIN","STUDENT")
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("api/enrollments").authenticated()
                .requestMatchers("/enrollments/**").hasAnyRole("STUDENT", "ADMIN", "PROFESSOR")
                .requestMatchers("/api/student/team").permitAll()
                .requestMatchers("/api/users").permitAll()
                .requestMatchers("/api/team-projects").permitAll()
                .requestMatchers("/api/teams").authenticated()
                .requestMatchers("/api/team-project-submissions/**").authenticated()
                .requestMatchers("/api/exams/{examId}/questions").hasAnyAuthority("STUDENT", "PROFESSOR")
                .requestMatchers("/api/exams/{examId}/questions/**").hasAnyAuthority("STUDENT", "PROFESSOR")
                .requestMatchers("/api/courses/{courseId}/exams").hasAnyAuthority("STUDENT", "PROFESSOR")
                .requestMatchers("/api/quizzes/").permitAll()
                .requestMatchers("/api/essay-questions/submit").hasAnyAuthority("STUDENT", "PROFESSOR")
                .requestMatchers("/api/courses/**").permitAll()
                .requestMatchers("/api/grade/**").permitAll()
                .requestMatchers("/api/grade/**").permitAll()

        

              
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtRequestFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // ✅ JWT 필터 추가
        
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> User.builder()
            .username(username)
            .password(passwordEncoder().encode("password")) // 기본 비밀번호 설정
            .roles("USER")
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(List.of(authProvider));
    }
}
