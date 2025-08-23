package org.example.flow.dto.shop.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDetailResponse {

    private ShopDto shop;
    private List<ImageDto> images;
    private List<RewardCouponDto> rewardCoupons;
    private List<BenefitRequirementDto> benefitRequirements;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopDto {
        private Long shopInfoId;
        private String explanationTitle;        // Place.explanationTitle or ShopInfo.explanationTitle
        private String location;
        private String explanationContent;  // ShopInfo.explanationContent
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {
        private Long shopImageId;
        private String image;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardCouponDto {
        private Long rewardCouponId;
        private String name;
        private Integer amount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BenefitRequirementDto {
        private Long benefitReqId;
        private String seedDetail;
        private Integer visitCount;
        private String couponType;
    }
}
