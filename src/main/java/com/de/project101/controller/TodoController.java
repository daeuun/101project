package com.de.project101.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.de.project101.dto.ResponseDTO;
import com.de.project101.dto.TodoDTO;
import com.de.project101.model.TodoEntity;
import com.de.project101.service.TodoService;

@RestController
@RequestMapping("todo")
public class TodoController {
	
	@Autowired
	private TodoService service;
	
	@GetMapping("/test")
	public ResponseEntity<?> testTodo() {
		String str = service.testService(); // 테스트 서비스 사용  
		List<String> list = new ArrayList<>();
		list.add(str);
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping
	public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto) {
		try {
			String temporaryUserId = "temporary-user"; // temporary user id
			
			// (1) TodoEntity로 반환한다.  
			TodoEntity entity = TodoDTO.toEntity(dto);
			
			// (2) id를 null로 초기화한다. 생성 당시에는 id가 없어야하기 때문
			entity.setId(null);
			
			// (3) 임시 사용자 아이디 설정.  
			entity.setUserId(temporaryUserId);
			
			// (4) 서비스를 이용해 Todo 엔티티 생성
			List<TodoEntity> entities = service.create(entity);
			
			// (5) 엔티티 리스트 => TodoDTO 리스트로 변환 
			List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
			
			// (6) 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
			
			// (7) ResponseDTO를 리턴
			return ResponseEntity.ok().body(response);
			
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}


}