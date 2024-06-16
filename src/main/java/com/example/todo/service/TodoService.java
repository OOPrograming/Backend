package com.example.todo.service;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.todo.model.TodoEntity;
import com.example.todo.persistence.TodoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅 기능을 제공하는 Lombok 어노테이션
@Service // 이 클래스가 서비스 레이어의 컴포넌트임을 명시

public class TodoService {

    @Autowired // TodoRepository를 자동으로 주입받음
    private TodoRepository repository;

    // Todo 항목을 생성하는 메서드
    public List<TodoEntity> create(final TodoEntity entity) {
        // 유효성 검사
        validate(entity);
        // 데이터베이스에 저장
        repository.save(entity);
        // 사용자 ID로 Todo 항목 목록을 반환
        return repository.findByUserId(entity.getUserId());
    }

    // 특정 사용자의 Todo 항목을 조회하는 메서드
    public List<TodoEntity> retrieve(final String userId) {
        return repository.findByUserId(userId);
    }

    // Todo 항목을 업데이트하는 메서드
    public List<TodoEntity> update(final TodoEntity entity) {
        // 유효성 검사
        validate(entity);
        // 엔티티가 존재하는지 확인 후 업데이트
        if (repository.existsById(entity.getId())) {
            repository.save(entity);
        } else {
            throw new RuntimeException("Unknown Id");
        }
        // 사용자 ID로 Todo 항목 목록을 반환
        return repository.findByUserId(entity.getUserId());
    }

    // Todo 항목을 삭제하는 메서드
    public List<TodoEntity> delete(final TodoEntity entity) {
        // 엔티티가 존재하는지 확인 후 삭제
        if (repository.existsById(entity.getId())) {
            repository.deleteById(entity.getId());
        } else {
            throw new RuntimeException("Id does not exist");
        }
        // 사용자 ID로 Todo 항목 목록을 반환
        return repository.findByUserId(entity.getUserId());
    }

    // 엔티티의 유효성을 검사하는 메서드
    public void validate(final TodoEntity entity) {
        // 엔티티가 null인지 확인
        if (entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("entity cannot be null");
        }
        // 사용자 ID가 null인지 확인
        if (entity.getUserId() == null) {
            log.warn("Unknown user");
            throw new RuntimeException("Unknown user");
        }
    }

}
