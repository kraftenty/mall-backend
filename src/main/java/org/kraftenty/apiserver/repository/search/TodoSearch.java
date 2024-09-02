package org.kraftenty.apiserver.repository.search;

import org.kraftenty.apiserver.domain.Todo;
import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
