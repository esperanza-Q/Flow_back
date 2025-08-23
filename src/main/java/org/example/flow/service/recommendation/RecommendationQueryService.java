package org.example.flow.service.recommendation;

import java.util.List;

public interface RecommendationQueryService {
    /** userId에게 추천되는 shopInfoId 리스트(상위 N개) */
    List<Long> getRecommendedShopIds(Long userId);
}
