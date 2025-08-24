package org.example.flow.repository;

import org.example.flow.entity.Visited;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitedRepository extends JpaRepository<Visited, Long> {

    long countByUser_UserIdAndShopInfo_ShopInfoId(Long userId, Long shopInfoId);

    //최근 방문일 리스트
    List<Visited> findTop10ByUser_UserIdAndShopInfo_ShopInfoIdOrderByCreatedAtDesc(Long userId, Long shopInfoId);
}
