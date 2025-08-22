package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.response.ShopDetailResponse;
import org.example.flow.entity.*;
import org.example.flow.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopInfoRepository shopInfoRepository;
    private final PlaceRepository placeRepository;
    private final ShopImageRepository shopImageRepository;
    private final RewardCouponRepository rewardCouponRepository;
    private final BenefitReqRepository benefitReqRepository;

    public ShopDetailResponse getShopDetail(Long shopInfoId) {
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Place place = placeRepository.findByShopInfo_ShopInfoId(shopInfoId);

        List<ShopImage> images = shopImageRepository.findByShopInfo_ShopInfoId(shopInfoId);
        List<RewardCoupon> rewardCoupons = rewardCouponRepository.findByShopInfo_ShopInfoId(shopInfoId);
        List<BenefitReq> benefitReqs = benefitReqRepository.findByShopInfo_ShopInfoId(shopInfoId);

        return ShopDetailResponse.builder()
                .shop(new ShopDetailResponse.ShopDto(
                        shopInfo.getShopInfoId(),
                        place.getName(),
                        place.getLocation(),
                        shopInfo.getExplanation()
                ))
                .images(images.stream()
                        .map(img -> new ShopDetailResponse.ImageDto(
                                img.getShopImageId(),
                                img.getImage()
                        ))
                        .collect(Collectors.toList()))
                .rewardCoupons(rewardCoupons.stream()
                        .map(c -> new ShopDetailResponse.RewardCouponDto(
                                c.getRewardCouponId(),
                                c.getName(),
                                c.getAmount()
                        ))
                        .collect(Collectors.toList()))
                .benefitRequirements(benefitReqs.stream()
                        .map(b -> new ShopDetailResponse.BenefitRequirementDto(
                                b.getBenefitReqId(),
                                b.getSeedDetail(),
                                b.getVisitCount(),
                                b.getCouponType()
                        ))
                        .collect(Collectors.toList()))
                .build();
    }
}
