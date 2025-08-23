package org.example.flow.dto.shopMypage.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateShopInfoResponse {

    private List<BusinessHoursDto> businessHours; // 기존
    private String explanationTitle;              // ⬅️ 추가
    private String explanationContent;            // ⬅️ 추가
    private List<ImageDto> images;                // 기존
    private boolean seedCondition;                // 기존
    private String seedDetail;                    // 기존
    private boolean couponCondition;              // 기존
    private Integer visitCount;                   // 기존
    private String couponType;                    // 기존
    private String couponImage;   // null 가능

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
