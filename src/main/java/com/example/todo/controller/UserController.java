package com.example.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.model.UserEntity;
import com.example.todo.security.TokenProvider;
import com.example.todo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 Lombok 어노테이션
@RestController // RESTful 컨트롤러임을 명시
@RequestMapping("/auth") // "/auth" 경로에 매핑
//@CrossOrigin(origins = "*") // CORS 설정 (주석 처리됨)
public class UserController {
    @Autowired
    private UserService userService; // UserService 빈 주입

    @Autowired
    private TokenProvider tokenProvider; // TokenProvider 빈 주입

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호 인코더

    // 사용자 등록 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            // UserEntity 생성 및 저장
            UserEntity user = UserEntity.builder()
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();

            UserEntity registeredUser = userService.create(user);

            // 응답용 UserDTO 생성
            UserDTO responseUserDTO = userDTO.builder()
                    .email(registeredUser.getEmail())
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .build();

            // 성공 응답 반환
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            // 예외 발생 시 에러 응답 반환
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 사용자 인증 엔드포인트
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword(), passwordEncoder);

        if (user != null) {
            // JWT 토큰 생성
            final String token = tokenProvider.create(user);

            // 응답용 UserDTO 생성
            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .token(token)
                    .build();

            // 성공 응답 반환
            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            // 로그인 실패 시 에러 응답 반환
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
