package com.lms.project.LMS.glances;

import com.lms.project.LMS.glances.EmailNotificationService; // `EmailNotificationService`의 올바른 경로 확인
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private EmailNotificationService emailService;

    @GetMapping("/send-email")
    public String sendEmail() {
        // 수신자, 제목, 본문 설정
        String to = "recipient@example.com";
        String subject = "Test Email Notification";
        String body = "This is a test email from Spring Boot.";

        // 이메일 전송
        emailService.sendEmail(to, subject, body);

        return "Email sent successfully!";
    }
}
