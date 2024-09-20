package org.kraftenty.apiserver.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.kraftenty.apiserver.domain.Cart;
import org.kraftenty.apiserver.domain.CartItem;
import org.kraftenty.apiserver.domain.Member;
import org.kraftenty.apiserver.domain.Product;
import org.kraftenty.apiserver.dto.CartItemListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class CartRepositoryTests {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    @Commit
    @Test
    public void testInsertByProduct() {
        String email = "user1@aaa.com";
        Long pno = 6L;
        int qty = 3;

        // 사용자의 이메일과 상품 번호로 장바구니 아이템이 있는지 확인
        // 없으면-> 추가, 있으면-> 수량 변경해서 저장
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);
        if (cartItem != null) { // 이미 사용자의 장바구니에 담겨있는 상품이라면
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);
            return;
        }
        // 사용자의 장바구니에 장바구니 아이템을 만들어서 저장해야함
        // 그런데, 장바구니 자체가 없을 수도 있음
        Optional<Cart> result = cartRepository.getCartOfMember(email);

        Cart cart = null;

        if (result.isEmpty()) {
            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();
            cart = cartRepository.save(tempCart); // 새로운 카트 하나 생성
        } else { // 장바구니는 있으나 해당 상품의 장바구니 아이템은 없는 경우
            cart = result.get();
        }

        Product product = Product.builder().pno(pno).build();
        cartItem = CartItem.builder().cart(cart).product(product).qty(qty).build();

        cartItemRepository.save(cartItem);


    }

    @Test
    public void testListOfMember() {
        String email = "user1@aaa.com";

        List<CartItemListDTO> cartItemListDTOList = cartItemRepository.getItemsOfCartDTOByEmail(email);
        for (CartItemListDTO dto : cartItemListDTOList) {
            log.info(dto);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testUpdateByCino() {
        Long cino = 1L;
        int qty = 4;

        Optional<CartItem> result = cartItemRepository.findById(cino);
        CartItem cartItem = result.orElseThrow();
        cartItem.changeQty(qty);
        cartItemRepository.save(cartItem);
    }

    // 삭제한 다음, 리스트 가져오기
    @Test
    public void testDeleteThenList() {
        Long cino = 1L;
        Long cno = cartItemRepository.getCartFromItem(cino);
        cartItemRepository.deleteById(cno);
        List<CartItemListDTO> cartItemListDTOList = cartItemRepository.getItemsOfCartDTOByCart(cino);

        for(CartItemListDTO dto : cartItemListDTOList) {
            log.info(dto);
        }
    }
}
