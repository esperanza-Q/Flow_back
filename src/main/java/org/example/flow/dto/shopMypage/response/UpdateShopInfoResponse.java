package org.example.flow.dto.shopMypage.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateShopInfoResponse {

    private List<BusinessHoursDto> business_hours;
    private String explanation;
    private List<ImageDto> images;
    private boolean seedCondition;
    private String seedDetail;    // null 가능
    private boolean couponCondition;
    private Integer visitCount;   // null 가능
    private String  couponType;   // null 가능
    private String  couponImage;  // null 가능

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class BusinessHoursDto {
        private String week;      // "MON/TUE/WED/..." (동일 시간대 묶음)
        private String openTime;  // "HH:mm"
        private String closeTime; // "HH:mm"
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ImageDto {
        private String image; // 매장 사진 URL
    }
}
