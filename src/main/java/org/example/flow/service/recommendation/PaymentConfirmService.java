package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentConfirmService {

    private final RecommendationVisitOrchestrator orchestrator;
    private final RewardService rewardService;

    @Transactional
    public Map<String, Object> confirm(Long userId, Long shopInfoId, long paidAmount) {
        Map<String, Object> res = new LinkedHashMap<>();

        // 1) 방문 소진(추천-방문 매칭)
        boolean matched = orchestrator.confirmVisitOnly(userId, shopInfoId);
        res.put("matched", matched);

        // 2) 보상: matched가 true일 때만 판단 (RewardService는 3인자)
        boolean rewarded = matched && rewardService.awardAiRecommendedPayment(userId, shopInfoId, paidAmount);
        res.put("aiPaymentRewarded", rewarded);

        // 3) 보상 OK 시에만 다음 추천
        if (rewarded) {
            Long nextId = orchestrator.getNextRecommendShopId(userId);
            res.put("nextRecommendShopId", nextId);
        }
        return res;
    }
}
