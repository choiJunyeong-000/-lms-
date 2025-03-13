package com.lms.project.LMS.glances;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component  // ✅ Spring Bean으로 등록
public class LiveUserMonitorApp {

    private final GlancesService glancesService;  // ✅ Spring에서 주입받을 서비스

    @Autowired
    public LiveUserMonitorApp(GlancesService glancesService) {  // ✅ 생성자 주입 사용
        this.glancesService = glancesService;
    }

    public void startMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                JSONObject metrics = glancesService.getMetricses();
                int activeUsers = metrics.getInt("active_users");

            } catch (Exception e) {
                
            }
        };

        // 5초마다 실행
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }
}
