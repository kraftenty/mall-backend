package org.kraftenty.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kraftenty.apiserver.domain.Product;
import org.kraftenty.apiserver.domain.ProductImage;
import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.kraftenty.apiserver.dto.PageResponseDTO;
import org.kraftenty.apiserver.dto.ProductDTO;
import org.kraftenty.apiserver.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending()
        );

        Page<Object[]> result = productRepository.selectList(pageable);
        List<ProductDTO> dtoList = result.get().map(arr -> {
            ProductDTO productDTO = null;
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build();

            String imageStr = productImage.getFileName();
            productDTO.setUploadFileNames(List.of(imageStr));

            return productDTO;
        }).collect(Collectors.toList());

        long totalCount = result.getTotalElements();
        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }


    @Override
    public Long register(ProductDTO productDTO) {
        Product product = dtoToEntity(productDTO);
        Long pno = productRepository.save(product).getPno();
        return pno;
    }

    @Override
    public ProductDTO get(Long pno) {
        Optional<Product> result = productRepository.findById(pno);
        Product product = result.orElseThrow();
        return entityToDTO(product);
    }

    @Override
    public void modify(ProductDTO productDTO) {
        // 조회
        Optional<Product> result = productRepository.findById(productDTO.getPno());
        Product product = result.orElseThrow();
        // 변경 내용 반영
        product.changePrice(productDTO.getPrice());
        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changeDel(productDTO.isDelFlag());
        // 이미지파일 처리
        List<String> uploadFileNames = productDTO.getUploadFileNames();
        product.clearList();
        if(uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(uploadName -> {
                product.addImageString(uploadName);
            });
        }
        // 저장
        productRepository.save(product);
    }

    @Override
    public void remove(Long pno) {
        productRepository.deleteById(pno);
    }

    // 편의 메서드
    private Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .build();
        List<String> uploadFileNames = productDTO.getUploadFileNames();
        if (uploadFileNames == null || uploadFileNames.isEmpty()) {
            return product;
        }

        uploadFileNames.forEach(fileName -> {
            product.addImageString(fileName);
        });
        return product;
    }

    private ProductDTO entityToDTO(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .delFlag(product.isDelFlag())
                .build();
        List<ProductImage> imageList = product.getImageList();
        if(imageList == null || imageList.isEmpty()) {
            return productDTO;
        }
        List<String> fileNameList = imageList.stream().map(productImage -> productImage.getFileName()).toList();
        productDTO.setUploadFileNames(fileNameList);
        return productDTO;
    }
}
