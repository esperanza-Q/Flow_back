package org.example.flow.dto.shopMypage.request;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShopInfoRequest {

    private List<BusinessHoursDto> business_hours;
    private String explanationTitle;   // 매장 타이틀
    private String explanationContent; // 매장 설명 본문
    private List<ImageDto> images;

    private Boolean seedCondition;
    private String seedDetail;

    private Boolean couponCondition;
    private Integer visitCount;
    private String couponType;
    private String couponImage;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessHoursDto {
        private String week;      // "MON", "TUE"...
        private LocalTime openTime;  // "09:00"
        private LocalTime closeTime; // "21:00"
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {
        private String image;
    }
}
