package org.kraftenty.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    // 상품 넘버
    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    // 삭제 플래그
    private boolean delFlag;

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    // 이미 업로드 되어 있는 파일들의 이름을 가지고 올 때
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();



}
