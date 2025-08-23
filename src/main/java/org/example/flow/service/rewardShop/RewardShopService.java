package org.example.flow.service.rewardShop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flow.dto.rewardShop.response.RewardShopResponseDTO;
import org.example.flow.entity.Profile;
import org.example.flow.entity.ReceiveRewardCoupon;
import org.example.flow.entity.RewardCoupon;
import org.example.flow.entity.User;
import org.example.flow.repository.ProfileRepository;
import org.example.flow.repository.ReceiveCouponRepository;
import org.example.flow.repository.ReceiveRewardCouponRepository;
import org.example.flow.repository.RewardCouponRepository;
import org.example.flow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardShopService {
    private final RewardCouponRepository rewardCouponRepository;
    private final ReceiveRewardCouponRepository receiveRewardCouponRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public List<RewardShopResponseDTO> getRewardCoupons() {
        // DB에서 모든 쿠폰 조회
        List<RewardCoupon> rewardCoupons = rewardCouponRepository.findAll();

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

    @Transactional
    public void receiveRewardCoupon(Long rewardCouponId) throws Exception {
        RewardCoupon rewardCoupon = rewardCouponRepository.findByRewardCouponId(rewardCouponId);

        User user = SecurityUtil.getCurrentUser();
        Profile profile = profileRepository.findByUser(user);

        if(profile.getPoint()<rewardCoupon.getAmount()){
            throw new Exception("포인트가 부족합니다!");
        }

        profile.setPoint(profile.getPoint()-rewardCoupon.getAmount());

        ReceiveRewardCoupon receiveRewardCoupon = ReceiveRewardCoupon.builder()
                .rewardCoupon(rewardCoupon)
                .user(user)
                .used(false)
                .build();

        receiveRewardCouponRepository.save(receiveRewardCoupon);
    }
}
