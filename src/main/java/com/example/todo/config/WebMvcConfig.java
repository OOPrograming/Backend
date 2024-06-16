package com.example.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 이 클래스가 Spring 설정 클래스임을 명시
public class WebMvcConfig implements WebMvcConfigurer {
    private final long MAX_AGE_SECS = 3600; // CORS 요청의 최대 허용 시간을 3600초(1시간)으로 설정

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 설정 적용
                .allowedOriginPatterns("*") // 모든 출처 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 지정
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 자격 증명(쿠키, 인증 헤더 등)을 포함한 요청 허용
                .maxAge(MAX_AGE_SECS); // 사전 요청(Preflight request)의 캐시 최대 시간 설정
    }
}
