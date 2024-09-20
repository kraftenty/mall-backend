package org.kraftenty.apiserver.service;

import org.kraftenty.apiserver.dto.CartItemDTO;
import org.kraftenty.apiserver.dto.CartItemListDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CartService {

    List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO);

    List<CartItemListDTO> getCartItems(String email);

    List<CartItemListDTO> remove(Long cino);

}
