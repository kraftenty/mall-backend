package org.kraftenty.apiserver.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kraftenty.apiserver.domain.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class TodoRepositoryTests {

    @Autowired
    public TodoRepository todoRepository;

    @Test
    public void test1() {
        Assertions.assertNotNull(todoRepository);
        log.info(todoRepository.getClass().getName());
    }

    @Test
    public void testInsert() {
        for (int i = 0; i < 100; i++) {
            Todo todo = Todo.builder()
                    .title("Title " + i)
                    .dueDate(LocalDate.of(2024,12,31))
                    .writer("user00")
                    .build();

            Todo result = todoRepository.save(todo);
        }

    }

    @Test
    public void testRead() {
        Long tno = 1L;
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow();

        log.info(todo);
    }

    @Test
    public void testUpdate() {
        Long tno = 1L;
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow();

        todo.changeTitle("Update Title");
        todo.changeComplete(true);

        todoRepository.save(todo);
    }

    @Test
    public void testPaging() {
        // 페이지 번호는 0부터 시작
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);

        log.info(result.getContent());

    }

}
