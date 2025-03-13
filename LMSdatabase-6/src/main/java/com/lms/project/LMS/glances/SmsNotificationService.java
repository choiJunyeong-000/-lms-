package com.lms.project.LMS.glances;

import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class SmsNotificationService {
    private static final String ACCOUNT_SID = "";
    private static final String AUTH_TOKEN = "";
    private static final String FROM_NUMBER = "";  // Twilio에서 제공한 발신 번호

    public SmsNotificationService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        System.out.println("✅ Twilio 초기화 완료!");  // Twilio 초기화 확인용 로그
    }

    public void sendSms(String to, String body) {
        try {
            Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to),  // 수신 번호
                new com.twilio.type.PhoneNumber(FROM_NUMBER),  // Twilio 발신 번호
                body
            ).create();

            System.out.println("✅ SMS 전송 성공! SID: " + message.getSid());
            System.out.println("📌 메시지 상태: " + message.getStatus());

        } catch (Exception e) {
            System.err.println("❌ Twilio SMS 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}