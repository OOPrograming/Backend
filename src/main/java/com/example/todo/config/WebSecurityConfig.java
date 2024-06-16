package com.example.todo.config;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.filter.CorsFilter;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.example.todo.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메서드 수준의 보안 활성화
@EnableWebSecurity // 웹 보안 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    private final ObjectMapper objectMapper;

    @Autowired
    public WebSecurityConfig(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.cors() // CORS 설정
                .and()
                .csrf()
                .disable() // CSRF 비활성화
                .httpBasic()
                .disable() // HTTP Basic 인증 비활성화
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용하지 않음
                .and()
                .authorizeRequests()
                .antMatchers("/","/auth/**","/h2-console/**").permitAll() // 특정 경로 접근 허용
                .anyRequest()
                .authenticated(); // 나머지 요청은 인증 필요
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> {
                    Map<String,Object> data = new HashMap<String, Object>();
                    data.put("status", HttpServletResponse.SC_FORBIDDEN);
                    data.put("message", e.getMessage());

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 인증 실패 시 403 상태 코드 반환
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    objectMapper.writeValue(response.getOutputStream(), data); // 응답 본문에 에러 메시지 작성
                });
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class); // JWT 인증 필터 추가
    }
}
