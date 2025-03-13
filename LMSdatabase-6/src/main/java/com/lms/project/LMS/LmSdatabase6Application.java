package com.lms.project.LMS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.lms.project.LMS.glances.LiveUserMonitorApp;

@SpringBootApplication
@ComponentScan(basePackages = "com.lms.project.LMS")
public class LmSdatabase6Application implements CommandLineRunner{
	    private final LiveUserMonitorApp liveUserMonitorApp;  // ✅ 생성자 주입 사용

	    @Autowired
	    public LmSdatabase6Application(LiveUserMonitorApp liveUserMonitorApp) {  // ✅ 생성자에서 주입
	        this.liveUserMonitorApp = liveUserMonitorApp;
	    }
	public static void main(String[] args) {
		SpringApplication.run(LmSdatabase6Application.class, args);
	}
	@Override
    public void run(String... args) {
        System.out.println("✅ LiveUserMonitorApp 실행 시작...");
        liveUserMonitorApp.startMonitoring();  // ✅ `LiveUserMonitorApp` 실행
    }

}
