// org.example.flow.repository.PaymentCheckRepository
package org.example.flow.repository;

import org.example.flow.entity.PaymentCheck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentCheckRepository extends JpaRepository<PaymentCheck, Long> {

    List<PaymentCheck> findByShopInfo_ShopInfoIdAndStatus(
            Long shopInfoId, PaymentCheck.STATUS status, Sort sort);

    // ✅ 유저/가게의 "최신 WAITING" 한 건 (재사용 용도)
    Optional<PaymentCheck> findTopByUser_UserIdAndShopInfo_ShopInfoIdAndStatusOrderByCreatedAtDesc(
            Long userId, Long shopInfoId, PaymentCheck.STATUS status);

    // ✅ 해당 id가 ACCEPT인지 확인(Verifier 대체/보조)
    @Query("select (pc.status = org.example.flow.entity.PaymentCheck.STATUS.ACCEPT) from PaymentCheck pc where pc.paymentCheckId = :id")
    Boolean isAccepted(@Param("id") Long paymentCheckId);


    /** 집계: ACCEPT */
    interface ShopPayCount { Long getShopInfoId(); Long getCnt(); }


    @Query("""
        select pc.shopInfo.shopInfoId as shopInfoId, count(pc) as cnt
        from PaymentCheck pc
        where pc.status = org.example.flow.entity.PaymentCheck.STATUS.ACCEPT
          and pc.createdAt >= :since
        group by pc.shopInfo.shopInfoId
        order by cnt desc
    """)
    List<ShopPayCount> findTopPaidShopsSince(@Param("since") LocalDateTime since, Pageable pageable);
}
