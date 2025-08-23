package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentConfirmService {
    private final RecommendationVerifier verifier;

    public Map<String, Object> confirm(Long userId, Long shopInfoId) {
        boolean aiPaymentRewarded = verifier.isRecommended(userId, shopInfoId, null);
        return Map.of("aiPaymentRewarded", aiPaymentRewarded);
    }
}
