package org.example.flow.repository;

import org.example.flow.entity.PaymentCheck;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentCheckRepository extends JpaRepository<PaymentCheck, Long> {

    // shopInfoId + status 로 조회 + 정렬 지원
    List<PaymentCheck> findByShopInfo_ShopInfoIdAndStatus(Long shopInfoId,
                                                          PaymentCheck.STATUS status,
                                                          Sort sort);
}
