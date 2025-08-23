package org.example.flow.repository;

import org.example.flow.entity.RewardCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardCouponRepository extends JpaRepository<RewardCoupon, Long> {
    List<RewardCoupon> findByShopInfo_ShopInfoId(Long shopInfoId);
    RewardCoupon findByRewardCouponId(Long rewardCouponId);
}