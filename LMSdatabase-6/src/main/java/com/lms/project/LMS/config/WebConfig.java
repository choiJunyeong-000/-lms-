package com.lms.project.LMS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
        	@Override
        	public void addCorsMappings(CorsRegistry registry) {
        		
        		registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 허용할 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        		
        		
        		
        	    registry.addMapping("/api/**")
        	            .allowedOrigins("http://localhost:3000")
        	            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        	            .allowedHeaders("*")
        	            .allowCredentials(true);
        	   

        	    // 다른 엔드포인트에 대한 CORS 설정 (필요에 따라 추가)
        	    registry.addMapping("/api/courses/download")
        	            .allowedOrigins("http://localhost:3000")
        	            .allowedMethods("GET")
        	            .allowedHeaders("*")
        	            .allowCredentials(true);

        	    registry.addMapping("/api/courses/upload-video")
        	            .allowedOrigins("http://localhost:3000")
        	            .allowedMethods("GET")
        	            .allowedHeaders("*")
        	            .allowCredentials(true);
        	    
        	    registry.addMapping("/uploads/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(true);
        	}

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**")  // '/uploads/' URL로 접근 허용
                        .addResourceLocations("file:C:/uploads/")  // 실제 폴더 경로 매핑
                        .setCachePeriod(3600); // 캐시 설정 (선택)
            }

        };
    }
    
}