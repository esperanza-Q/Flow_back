package org.example.flow.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@Data
public class ShopInfoWriteRequestDTO {
    private Long shopInfoId;
    private List<BusinessHoursDTO>  businessHours;
    private String explanationTitle;
    private String explanationContent;
    private List<MultipartFile> images;
    private Boolean seedCondition;
    private String seedDetail;
    private Boolean couponCondition;
    private Integer visitCount;
    private String couponType;
    private MultipartFile couponImage;
}
