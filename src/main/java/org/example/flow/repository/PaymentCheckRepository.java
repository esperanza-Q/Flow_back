package org.example.flow.repository;

import org.example.flow.entity.PaymentCheck; // 프로젝트 실제 경로/클래스명으로
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentCheckRepository extends JpaRepository<PaymentCheck, Long> {
    List<PaymentCheck> findByShopInfo_ShopInfoIdAndStatus(Long shopInfoId, PaymentCheck.STATUS status, Sort sort);
    interface ShopPayCount {
        Long getShopInfoId();
        Long getCnt();
    }

    @Query("""
        select pc.shopInfo.shopInfoId as shopInfoId, count(pc) as cnt
        from PaymentCheck pc
        where pc.status = 'COMPLETED'    
          and pc.createdAt >= :since     
        group by pc.shopInfo.shopInfoId
        order by cnt desc
    """)
    List<ShopPayCount> findTopPaidShopsSince(@Param("since") LocalDateTime since, Pageable pageable);
}
