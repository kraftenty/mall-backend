package org.kraftenty.apiserver.repository.search;

import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.kraftenty.apiserver.dto.PageResponseDTO;
import org.kraftenty.apiserver.dto.ProductDTO;

public interface ProductSearch {

    PageResponseDTO<ProductDTO> searchList(PageRequestDTO pageRequestDTO);
}
