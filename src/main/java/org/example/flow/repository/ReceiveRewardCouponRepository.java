package org.example.flow.repository;

import org.example.flow.entity.ReceiveRewardCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiveRewardCouponRepository extends JpaRepository<ReceiveRewardCoupon,Long> {
    // Reward 쿠폰
    List<ReceiveRewardCoupon> findByUser_UserIdAndRewardCoupon_ShopInfo_ShopInfoId(Long userId, Long shopInfoId);

}
