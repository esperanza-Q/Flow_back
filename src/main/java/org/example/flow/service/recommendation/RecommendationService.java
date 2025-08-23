package org.example.flow.service.recommendation;

public interface RecommendationService {
    boolean isRecommended(Long userId, Long shopInfoId);
}
