package com.example.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.todo.model.UserEntity;
import com.example.todo.persistence.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅 기능을 제공하는 Lombok 어노테이션
@Service // 이 클래스가 서비스 레이어의 컴포넌트임을 명시

public class UserService {

    @Autowired // UserRepository를 자동으로 주입받음
    private UserRepository userRepository;

    // 사용자 생성 메서드
    public UserEntity create(final UserEntity userEntity) {
        // 유효하지 않은 인자가 전달되었을 경우 예외를 발생시킴
        if(userEntity == null || userEntity.getEmail() == null) {
            throw new RuntimeException("Invalid arguments");
        }

        final String email = userEntity.getEmail();
        // 이미 존재하는 이메일일 경우 예외를 발생시킴
        if(userRepository.existsByEmail(email)) {
            log.warn("Email already exists {}", email); // 경고 로그 출력
            throw new RuntimeException("Email already exists");
        }
        // 새로운 사용자 엔티티를 데이터베이스에 저장
        return userRepository.save(userEntity);
    }

    // 사용자 인증 메서드
    public UserEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
        // 이메일로 사용자 엔티티를 조회
        final UserEntity originalUser = userRepository.findByEmail(email);
        // 사용자 엔티티가 존재하고, 비밀번호가 일치할 경우 해당 사용자 엔티티를 반환
        if(originalUser != null && encoder.matches(password, originalUser.getPassword())){
            return originalUser;
        }
        // 인증 실패 시 null을 반환
        return null;
    }
}
