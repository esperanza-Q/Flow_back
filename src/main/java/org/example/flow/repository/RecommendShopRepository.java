package org.example.flow.repository;

import jakarta.persistence.LockModeType;
import org.example.flow.entity.RecommendShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface RecommendShopRepository extends JpaRepository<RecommendShop, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RecommendShop> findTopByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // ✅ 동시성 방지
    Optional<RecommendShop>
    findTopByUser_UserIdAndShopInfo_ShopInfoIdAndVisitedFalseOrderByCreatedAtDesc(Long userId, Long shopInfoId);


}


