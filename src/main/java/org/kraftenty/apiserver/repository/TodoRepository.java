package org.kraftenty.apiserver.repository;

import org.kraftenty.apiserver.domain.Todo;
import org.kraftenty.apiserver.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
