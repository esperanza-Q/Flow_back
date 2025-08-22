package org.example.flow.dto.shop.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ShopDetailResponse {

    private ShopDto shop;
    private List<ImageDto> images;
    private List<RewardCouponDto> rewardCoupons;
    private List<BenefitRequirementDto> benefitRequirements;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ShopDto {
        private Long shopInfoId;
        private String name;
        private String location;
        private String explanation;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ImageDto {
        private Long shopImageId;
        private String imageUrl;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class RewardCouponDto {
        private Long rewardCouponId;
        private String name;
        private Integer amount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class BenefitRequirementDto {
        private Long pointReqId;
        private String seedDetail;
        private Integer visitCount;
        private String couponType;
    }
}
