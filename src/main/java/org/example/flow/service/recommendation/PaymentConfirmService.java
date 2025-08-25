package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.example.flow.repository.PaymentCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentConfirmService {

    private final RecommendationVisitOrchestrator orchestrator;
    private final RewardService rewardService;
    private final PaymentCheckRepository paymentCheckRepository;

    // org.example.flow.service.recommendation.PaymentConfirmService
    @Transactional
    public Map<String, Object> confirm(
            Long paymentCheckId,
            Long userId,
            Long shopInfoId,
            long amount,
            boolean forceCountOnAccept
    ) {
        if (!Boolean.TRUE.equals(paymentCheckRepository.isAccepted(paymentCheckId))) {
            return Map.of("accepted", false, "matched", false);
        }

        boolean matched = orchestrator.confirmVisitOnly(userId, shopInfoId);

        if (matched) {
            // ─ matched == true: 기존과 동일 (슬라이딩 + 임계 보상 + next)
            var wk = rewardService.increaseCountAndAwardThresholds(userId);
            int slidingPoints = rewardService.awardAiPaymentPoints(userId, amount);
            int bonusPoints   = (wk.awarded50() ? 50 : 0) + (wk.awarded100() ? 100 : 0);
            int totalPoints   = slidingPoints + bonusPoints;
            Long nextId       = orchestrator.getNextRecommendShopId(userId);

            Map<String,Object> res = new LinkedHashMap<>();
            res.put("accepted", true);
            res.put("matched", true);
            res.put("paymentPoints", totalPoints);
            res.put("counted", wk.counted());
            res.put("countThisWeek", wk.countThisWeek());
            res.put("awarded50", wk.awarded50());
            res.put("awarded100", wk.awarded100());
            res.put("nextRecommendShopId", nextId);
            return res;
        } else {
            // ─ matched == false
            if (forceCountOnAccept) {
                // ✅ 첫 승인: 카운트 + (필요시) 임계 보상 + 슬라이딩 포인트까지 모두 반영
                var wk = rewardService.increaseCountAndAwardThresholds(userId);
                int slidingPoints = rewardService.awardAiPaymentPoints(userId, amount); // ← 이제 unmatched여도 지급
                int bonusPoints   = (wk.awarded50() ? 50 : 0) + (wk.awarded100() ? 100 : 0);
                int totalPoints   = slidingPoints + bonusPoints;

                return Map.of(
                        "accepted", true,
                        "matched", false,
                        "paymentPoints", totalPoints,     // ← 합산 결과
                        "counted", wk.counted(),
                        "countThisWeek", wk.countThisWeek(),
                        "awarded50", wk.awarded50(),
                        "awarded100", wk.awarded100()
                );
            } else {
                // 재호출은 멱등: 카운트/포인트 변화 없음
                return Map.of(
                        "accepted", true,
                        "matched", false,
                        "paymentPoints", 0,
                        "counted", false,
                        "countThisWeek", 0,
                        "awarded50", false,
                        "awarded100", false
                );
            }
        }
    }
}