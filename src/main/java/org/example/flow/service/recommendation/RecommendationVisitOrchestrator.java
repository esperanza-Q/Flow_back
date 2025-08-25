package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.RecommendVisitLog;
import org.example.flow.entity.Visited;
import org.example.flow.entity.ShopInfo;
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
    private final RecommendVisitLogRepository visitLogRepository;
    private final UserRepository userRepository;
    private final ShopInfoRepository shopInfoRepository;
    private final ShopRecommendationService shopRecommendationService;

    @Transactional
    public boolean confirmVisitOnly(Long userId, Long shopInfoId) {
        var opt = recommendShopRepository
                .findTopByUser_UserIdAndShopInfo_ShopInfoIdAndVisitedFalseOrderByCreatedAtDesc(userId, shopInfoId);
        if (opt.isEmpty()) return false;

        var now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 1) 추천 소진
        var rs = opt.get();
        rs.setVisited(true);
        rs.setVisitedAt(now);

        // 2) visited INSERT (setter 방식)
        var userRef = userRepository.getReferenceById(userId);
        var shopRef = shopInfoRepository.getReferenceById(shopInfoId);
        var v = new Visited();
        v.setUser(userRef);
        v.setShopInfo(shopRef);
        v.setCreatedAt(now);
        visitedRepository.save(v);

        // 3) 주간 로그 upsert (RecommendVisitLog = weekStart, visitCount만 사용)
        upsertWeeklyVisitLog(userId, now.toLocalDate());

        return true;
    }

    @Transactional
    public Long getNextRecommendShopId(Long userId) {
        var user = userRepository.getReferenceById(userId);
        var next = shopRecommendationService.recommendShop(user, LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        return next != null ? next.getShopInfoId() : null;
    }

    private void upsertWeeklyVisitLog(Long userId, LocalDate today) {
        // 월요일 시작 주차
        LocalDate weekStart = today.minusDays((today.getDayOfWeek().getValue() + 6) % 7);

        var log = visitLogRepository.findByUser_UserIdAndWeekStart(userId, weekStart)
                .orElseGet(() -> {
                    var nl = new RecommendVisitLog();
                    nl.setUser(userRepository.getReferenceById(userId));
                    nl.setWeekStart(weekStart);
                    nl.setVisitCount(0);
                    return nl;
                });

        log.setVisitCount(log.getVisitCount() + 1);
        visitLogRepository.save(log);
    }
}
