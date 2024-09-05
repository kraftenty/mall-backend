package org.kraftenty.apiserver.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.kraftenty.apiserver.domain.Product;
import org.kraftenty.apiserver.domain.QProduct;
import org.kraftenty.apiserver.domain.QProductImage;
import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.kraftenty.apiserver.dto.PageResponseDTO;
import org.kraftenty.apiserver.dto.ProductDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }

    @Override
    public PageResponseDTO<ProductDTO> searchList(PageRequestDTO pageRequestDTO) {

        log.info("------------------------searchList 동작 중----------------");

        Pageable pageable = PageRequest.of(
            pageRequestDTO.getPage()-1,
            pageRequestDTO.getSize(),
            Sort.by("pno").descending()
        );

        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        JPQLQuery<Product> query = from(product)
            .leftJoin(product.imageList, productImage)
                .where(productImage.ord.eq(0));

        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);

        List<Product> productList = query.fetch();
        long count = query.fetchCount();

        log.info("================================");
        log.info(productList);


        return null;
    }
}
