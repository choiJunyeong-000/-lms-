package com.lms.project.LMS.glances;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GlancesService {
	@Autowired
	private EmailNotificationService emailService;
	@Autowired
	private SmsNotificationService smsService;

	private static final double AVG_RX_PER_USER = 500000; // 예시 값, 실제 환경에 맞게 조정
	private static final double AVG_TX_PER_USER = 100000; // 예시 값, 실제 환경에 맞게 조정
	private static final int USER_ALERT_THRESHOLD = 20;
	private static final long ALERT_COOLDOWN = 60000; // 60초 (1분) 쿨다운 적용
	private long lastAlertTime = 0; // 마지막 알림 전송 시간
	private boolean alertSent = false;
	
	
	@PostConstruct
    public void init() {
        System.out.println("✅ GlancesService 초기화 완료");
        System.out.println("📧 Email Service: " + emailService);
        System.out.println("📲 SMS Service: " + smsService);
    }

    public JSONObject getMetrics() throws Exception {
        String url = "http://localhost:61208/api/3/all"; // Glances API 주소
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response); // ✅ 문자열을 JSONObject로 변환
        } catch (Exception e) {
            throw new Exception("Unable to fetch metrics from Glances API", e);
        }
    }
    
    public JSONObject getMetricses() throws Exception {
        String url = "http://localhost:61208/api/3/all"; // Glances API 주소
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            // 네트워크 인터페이스 리스트에서 "eth0" 찾기
            JSONArray networkArray = json.getJSONArray("network");
            double rx = 0, tx = 0;

            for (int i = 0; i < networkArray.length(); i++) {
                JSONObject net = networkArray.getJSONObject(i);
                if (net.getString("interface_name").equals("eth0")) { // eth0 기준으로 데이터 추출
                    rx = net.getDouble("rx"); // 다운로드 속도 (bytes/s)
                    tx = net.getDouble("tx"); // 업로드 속도 (bytes/s)
                    break;
                }
            }

            // 접속자 수 추정
            int estimatedUsers = (int) ((rx + tx) / (AVG_RX_PER_USER + AVG_TX_PER_USER));
            estimatedUsers = Math.max(estimatedUsers, 1); // 최소 1명으로 설정

            // 🚨 90명 이상 접속 시 알림 발송
            if (estimatedUsers >= USER_ALERT_THRESHOLD && !alertSent) {
                
                sendAlert(estimatedUsers);
                alertSent = true; // 중복 발송 방지
            }

            // 접속자가 다시 80명 이하로 떨어지면 alertSent 초기화
            if (estimatedUsers < 10 && alertSent) {
                
                alertSent = false;
            }

            // JSON 응답 생성
            JSONObject result = new JSONObject();
            result.put("rx", rx / 1e3); // KB 단위 변환
            result.put("tx", tx / 1e3); // KB 단위 변환
            result.put("active_users", estimatedUsers);

            return result;

        } catch (Exception e) {
          
            throw new Exception("Unable to fetch metrics from Glances API", e);
        }
    }
    // 이메일 및 SMS 알림을 보내는 메서드
    private void sendAlert(int users) {
        long currentTime = System.currentTimeMillis();

        // 🚨 쿨다운 체크: 마지막 알림 이후 60초 이상 지나야 새로운 알림 전송
        if (currentTime - lastAlertTime < ALERT_COOLDOWN) {
            System.out.println("⏳ 알림 쿨다운 중... (최소 60초 대기)");
            return;
        }

        System.out.println("🚨 sendAlert 호출됨! 현재 접속자 수: " + users);
        String subject = "🚨 서버 과부하 경고!";
        String body = String.format("⚠️ 현재 접속자 수: %d명\n서버가 과부하 상태에 도달할 가능성이 있습니다.\n즉시 확인 필요!", users);

        try {
            emailService.sendEmail("", subject, body);
            System.out.println("✅ 이메일 전송 완료!");

            smsService.sendSms("", body);
            System.out.println("✅ SMS 전송 완료!");

            // ✅ 알림 전송 시간 업데이트
            lastAlertTime = currentTime;

        } catch (Exception e) {
            System.err.println("❌ 알림 전송 중 오류 발생: " + e.getMessage());
        }
    }
        
            

    
    public int getActiveConnections() {
        int activeConnections = 0;
        try {
            // `netstat -tn` 명령어로 현재 TCP 연결 정보를 가져옴
            Process process = Runtime.getRuntime().exec("netstat -tn");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 각 연결에 대해 'ESTABLISHED' 상태인 것만 계산
                if (line.contains("ESTABLISHED")) {
                    activeConnections++;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activeConnections;
    }
    
    
}
