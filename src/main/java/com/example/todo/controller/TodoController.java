package com.example.todo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.TodoDTO;
import com.example.todo.model.TodoEntity;
import com.example.todo.service.TodoService;

import lombok.extern.slf4j.Slf4j;

//@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("todo")

public class TodoController {
    @Autowired
    private TodoService service;

    @PostMapping
    public ResponseEntity<?>createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
        try {
			/*POST localhost:8080/todo
			{
				"title" : "My first todo", "done" : false
			}
			*/
            //log.info("Log : createTodo entrance");

            //dto 이용해 테이블에 저장하기 위한 entity 생성
            TodoEntity entity = TodoDTO.toEntity(dto);
            //log.info("Log:dto => entity ok!");

            //entity userId를 임시로 지정
            entity.setId(null);
            entity.setUserId(userId);

            //service.create를 통해 repository에 entity 저장
            //넘어오는 값이 없으수도 있으므로 List가 아닌 Optional로 함
            List<TodoEntity> entities = service.create(entity);
            //log.info("Log:service.create ok!");

            //entities를 dtos로 스트림 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            log.info("Log:entities => dtos ok!");

            //Response DTO생성
            /*{
             * "error" : null, "data" : [{"id":"000", "title" : "My first todo", "done" : false}}
             * */
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
            log.info("Log:responsedto ok!");

            //HTTP status 200 상태로 response 전송
            return ResponseEntity.ok().body(response);
        }
        catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);

        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveTodo(@AuthenticationPrincipal String userId){
        List<TodoEntity> entities = service.retrieve(userId);
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //http status 200 상태로 response 전송
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> upsdateTodo(
            @AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto){
        try {
            //dto를 이용해 테이블에 저장하기 위한 entity 생성
            TodoEntity entity = TodoDTO.toEntity(dto);

            //entity userID 임시로 저장
            entity.setUserId(userId);

            //service.create를 통해 repository에 entity 저장
            List<TodoEntity> entities = service.update(entity);

            //entities를 dtos로 스트림 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //ResponseDTO를 생성
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //https 200
            return ResponseEntity.ok().body(response);
        }catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(
            @AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto){
        try {
            TodoEntity entity = TodoDTO.toEntity(dto);
            //entity userID 임시로 저장
            entity.setUserId(userId);

            //service.create를 통해 repository에 entity 저장
            List<TodoEntity> entities = service.delete(entity);

            //entities를 dtos로 스트림 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            //ResponseDTO를 생성
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //https 200
            return ResponseEntity.ok().body(response);
        }catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }

    }

}