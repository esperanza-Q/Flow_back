package org.example.flow.dto.shopMypage.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShopInfoRequest {

    private List<BusinessHoursDto> business_hours;
    private String explanation;
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
        private String openTime;  // "09:00"
        private String closeTime; // "21:00"
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {
        private String image;
    }
}
