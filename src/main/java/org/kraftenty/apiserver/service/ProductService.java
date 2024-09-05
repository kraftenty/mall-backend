package org.kraftenty.apiserver.service;

import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.kraftenty.apiserver.dto.PageResponseDTO;
import org.kraftenty.apiserver.dto.ProductDTO;

public interface ProductService {

    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);

    Long register(ProductDTO productDTO);

    ProductDTO get(Long pno);

    void modify(ProductDTO productDTO);

    void remove(Long pno);
}
