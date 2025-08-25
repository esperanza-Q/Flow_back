package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.response.ShopDetailResponse;
import org.example.flow.entity.*;
import org.example.flow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopInfoRepository shopInfoRepository;
    private final PlaceRepository placeRepository;
    private final ShopImageRepository shopImageRepository;
    private final RewardCouponRepository rewardCouponRepository;
    private final BenefitReqRepository benefitReqRepository;

    public ShopDetailResponse getShopDetail(Long shopInfoId) {
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new RuntimeException("ShopInfo not found: " + shopInfoId));

        // 1) 장소 정보 (없을 수 있음)
        Place place = placeRepository.findByShopInfoShopInfoId(shopInfoId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        String explanationTitle =
                (place != null && place.getExplanationTitle() != null && !place.getExplanationTitle().isBlank())
                        ? place.getExplanationTitle()
                        : shopInfo.getExplanationTitle();

        String location = (place != null) ? place.getLocation() : null;

        // 2) 이미지/쿠폰/혜택 조건
        List<ShopImage> images = shopImageRepository.findByShopInfo_ShopInfoId(shopInfoId);
        List<RewardCoupon> rewardCoupons = rewardCouponRepository.findByShopInfo_ShopInfoId(shopInfoId);
        List<BenefitReq> benefitReqs = benefitReqRepository.findByShopInfo_ShopInfoId(shopInfoId);

        // 3) DTO 조립
        ShopDetailResponse.ShopDto shopDto = new ShopDetailResponse.ShopDto(
                shopInfo.getShopInfoId(),
                explanationTitle,                  // Place.explanationTitle 우선
                location,
                shopInfo.getExplanationContent()
        );

        List<ShopDetailResponse.ImageDto> imageDtos = images.stream()
                .map(img -> new ShopDetailResponse.ImageDto(
                        img.getShopImageId(),
                        img.getImage()
                ))
                .toList();

        List<ShopDetailResponse.RewardCouponDto> couponDtos = rewardCoupons.stream()
                .map(c -> new ShopDetailResponse.RewardCouponDto(
                        c.getRewardCouponId(),
                        c.getName(),
                        c.getAmount()
                ))
                .toList();

        List<ShopDetailResponse.BenefitRequirementDto> benefitDtos = benefitReqs.stream()
                .map(b -> new ShopDetailResponse.BenefitRequirementDto(
                        b.getBenefitReqId(),
                        b.getSeedDetail(),
                        b.getVisitCount(),
                        toCouponTypeString(b.getCouponType()) // Enum/String 모두 대응
                ))
                .toList();

        return ShopDetailResponse.builder()
                .shop(shopDto)
                .images(imageDtos)
                .rewardCoupons(couponDtos)
                .benefitRequirements(benefitDtos)
                .build();
    }

    /**
     * couponType을 DTO(String)으로 안전 변환
     * - Enum이면 enum.name()
     * - String/기타면 toString()
     * - null이면 null
     */
    private String toCouponTypeString(Object couponType) {
        if (couponType == null) return null;
        if (couponType instanceof Enum<?> e) return e.name();
        return couponType.toString();
    }
}
