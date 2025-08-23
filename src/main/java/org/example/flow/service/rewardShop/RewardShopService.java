package org.example.flow.service.rewardShop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flow.dto.rewardShop.response.RewardShopResponseDTO;
import org.example.flow.entity.RewardCoupon;
import org.example.flow.repository.RewardShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardShopService {
    private final RewardShopRepository rewardShopRepository;

    @Transactional
    public List<RewardShopResponseDTO> getRewardCoupons() {
        // DB에서 모든 쿠폰 조회
        List<RewardCoupon> rewardCoupons = rewardShopRepository.findAll();

        // 엔티티 -> DTO 변환
        List<RewardShopResponseDTO> dtoList = rewardCoupons.stream()
                .map(coupon -> new RewardShopResponseDTO(
                        coupon.getRewardCouponId(),         // rewardCouponId
                        coupon.getName(),       // name
                        coupon.getImage(),      // image
                        coupon.getAmount(), // amount
                        coupon.getShopInfo().getUser().getNickname()    // shopName
                ))
                .toList();

        return dtoList;
    }
}
