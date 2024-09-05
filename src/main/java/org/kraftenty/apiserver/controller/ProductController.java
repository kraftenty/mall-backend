package org.kraftenty.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kraftenty.apiserver.dto.PageRequestDTO;
import org.kraftenty.apiserver.dto.PageResponseDTO;
import org.kraftenty.apiserver.dto.ProductDTO;
import org.kraftenty.apiserver.service.ProductService;
import org.kraftenty.apiserver.util.CustomFileUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;

    private final ProductService productService;

    // 등록
    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO) {
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadedFileNames = fileUtil.saveFiles(files);

        productDTO.setUploadFileNames(uploadedFileNames); // DB에는 파일 이름만 저장

        log.info(uploadedFileNames);
        Long pno = productService.register(productDTO);

        return Map.of("RESULT", pno);
    }

    // 파일 단건 조회
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }

    // 리스트로 조회
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        return productService.getList(pageRequestDTO);
    }

    // 상품 단건 조회
    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    // 상품 수정
    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable("pno") Long pno, ProductDTO productDTO) {
        productDTO.setPno(pno);

        // DB에 저장된 원래 상품을 가져옴
        ProductDTO oldProductDTO = productService.get(pno);

        // 새로운 파일 업로드
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        // 기존에 있는 파일 리스트
        List<String> uploadedFileNames = productDTO.getUploadFileNames();
        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDTO);

        // 사용하지 않는 파일 제거
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName))
                    .toList();
            fileUtil.deleteFiles(removeFiles);
        }

        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable("pno") Long pno) {
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();
        productService.remove(pno);
        fileUtil.deleteFiles(oldFileNames);
        return Map.of("RESULT", "SUCCESS");
    }

}
