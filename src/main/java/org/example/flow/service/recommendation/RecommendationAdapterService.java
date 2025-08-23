// 어댑터: 리스트에 포함되는지로 판정
package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationAdapterService implements RecommendationService {

    private final RecommendationQueryService recommendationQueryService; // 이미 만든 쿼리 서비스

    @Override
    public boolean isRecommended(Long userId, Long shopInfoId) {
        List<Long> ids = recommendationQueryService.getRecommendedShopIds(userId);
        return ids != null && ids.contains(shopInfoId);
    }
}
