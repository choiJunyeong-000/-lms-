package com.lms.project.LMS.glances;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender; // JavaMailSender를 주입받음

    /**
     * 이메일 전송 메서드
     *
     * @param to      수신자 이메일
     * @param subject 이메일 제목
     * @param body    이메일 본문
     */
    public void sendEmail(String to, String subject, String body) {
        // SimpleMailMessage 객체 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);           // 수신자 이메일
        message.setSubject(subject); // 이메일 제목
        message.setText(body);       // 이메일 본문

        // 이메일 전송
        mailSender.send(message);
    }
}

