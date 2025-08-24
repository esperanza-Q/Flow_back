package org.example.flow.repository;

import org.example.flow.entity.RecommendShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendShopRepository extends JpaRepository<RecommendShop, Long> {
    Optional<RecommendShop> findTopByUser_UserIdOrderByCreatedAtDesc(Long userId);
}

