package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.Visited;
import org.example.flow.repository.*;
import org.example.flow.service.recommendShop.ShopRecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
@RequiredArgsConstructor
public class RecommendationVisitOrchestrator {

    private final RecommendShopRepository recommendShopRepository;
    private final VisitedRepository visitedRepository;
    private final UserRepository userRepository;
    private final ShopInfoRepository shopInfoRepository;
    private final ShopRecommendationService shopRecommendationService;

    /** ✅ 방문 소진만 수행 (주간 카운트 절대 금지) */
    // org.example.flow.service.recommendation.RecommendationVisitOrchestrator
    @Transactional
    public boolean confirmVisitOnly(Long userId, Long shopInfoId) {
        var latestOpt = recommendShopRepository.findTopByUser_UserIdOrderByCreatedAtDesc(userId);
        if (latestOpt.isEmpty()) return false;

        var rs = latestOpt.get();
        if (Boolean.TRUE.equals(rs.getVisited())) return false;
        if (!rs.getShopInfo().getShopInfoId().equals(shopInfoId)) return false;

        var now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        rs.setVisited(true);
        rs.setVisitedAt(now);

        var userRef = userRepository.getReferenceById(userId);
        var shopRef = shopInfoRepository.getReferenceById(shopInfoId);

        var v = new Visited();
        v.setUser(userRef);
        v.setShopInfo(shopRef);
        v.setCreatedAt(now);
        visitedRepository.save(v);

        return true;
    }


    @Transactional
    public Long getNextRecommendShopId(Long userId) {
        var user = userRepository.getReferenceById(userId);
        var next = shopRecommendationService.recommendShop(user, LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        return next != null ? next.getShopInfoId() : null;
    }
}
