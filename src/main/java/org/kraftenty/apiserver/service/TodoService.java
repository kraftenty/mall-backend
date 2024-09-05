package org.kraftenty.apiserver.service;

import org.kraftenty.apiserver.domain.Todo;
import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.kraftenty.apiserver.dto.PageResponseDTO;
import org.kraftenty.apiserver.dto.TodoDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TodoService {

    TodoDTO get(Long tno);

    Long register(TodoDTO todoDTO);

    void modify(TodoDTO dto);

    void remove(Long tno);

    PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO);

    default TodoDTO entityToDTO(Todo todo) {
        TodoDTO todoDTO = TodoDTO.builder()
                .tno(todo.getTno())
                .title(todo.getTitle())
                .writer(todo.getWriter())
                .complete(todo.isComplete())
                .dueDate(todo.getDueDate())
                .build();
        return todoDTO;
    }

    default Todo dtoToEntity(TodoDTO todoDTO) {
        Todo todo = Todo.builder()
                .tno(todoDTO.getTno())
                .title(todoDTO.getTitle())
                .writer(todoDTO.getWriter())
                .complete(todoDTO.isComplete())
                .dueDate(todoDTO.getDueDate())
                .build();
        return todo;
    }


}
