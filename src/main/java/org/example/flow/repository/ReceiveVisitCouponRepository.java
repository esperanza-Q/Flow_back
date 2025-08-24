package org.example.flow.repository;


import org.example.flow.entity.ReceiveVisitCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiveVisitCouponRepository extends JpaRepository<ReceiveVisitCoupon, Long> {
    // Visit 쿠폰
    List<ReceiveVisitCoupon> findByUser_UserIdAndBenefitReq_ShopInfo_ShopInfoId(Long userId, Long shopInfoId);
}

