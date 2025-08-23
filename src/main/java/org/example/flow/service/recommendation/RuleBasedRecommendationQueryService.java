package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.example.flow.repository.PaymentCheckRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // JDK 8 호환

@Service
@RequiredArgsConstructor
public class RuleBasedRecommendationQueryService implements RecommendationQueryService {

    private final PaymentCheckRepository paymentCheckRepository;

    private static final int TOP_N = 50;
    private static final int LOOKBACK_DAYS = 30;

    @Override
    public List<Long> getRecommendedShopIds(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusDays(LOOKBACK_DAYS);
        var top = paymentCheckRepository.findTopPaidShopsSince(since, PageRequest.of(0, TOP_N));
        return top.stream()
                .map(PaymentCheckRepository.ShopPayCount::getShopInfoId)
                .collect(Collectors.toList()); // JDK16+면 .toList() 가능
    }
}
