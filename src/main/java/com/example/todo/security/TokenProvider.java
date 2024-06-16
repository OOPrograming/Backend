package com.example.todo.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.todo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 Lombok 어노테이션
@Service // 이 클래스가 스프링 서비스 컴포넌트임을 명시
public class TokenProvider {
    private static final String SECRET_KEY = "NMA8JPctFuna59f5"; // JWT 서명에 사용할 비밀 키

    // 주어진 사용자 정보로 JWT 생성
    public String create(UserEntity userEntity) {
        // 토큰 만료 시간 설정 (현재 시간 + 1일)
        Date expireDate = Date.from(
                Instant.now()
                        .plus(1, ChronoUnit.DAYS));

        // JWT 생성 및 반환
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // 서명 알고리즘 및 비밀 키 설정
                .setSubject(userEntity.getId()) // 토큰 주제 (사용자 ID) 설정
                .setIssuer("todo app") // 발행자 설정
                .setIssuedAt(new Date()) // 발행 시간 설정
                .setExpiration(expireDate) // 만료 시간 설정
                .compact();
    }

    // 주어진 JWT 토큰을 검증하고 사용자 ID를 반환
    public String validateAndGetUserId(String token) {
        // 토큰을 파싱하여 클레임을 추출
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) // 비밀 키 설정
                .parseClaimsJws(token) // 토큰 파싱
                .getBody();

        // 토큰의 주제 (사용자 ID) 반환
        return claims.getSubject();
    }
}