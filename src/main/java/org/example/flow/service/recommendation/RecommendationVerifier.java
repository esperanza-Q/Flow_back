package org.example.flow.service.recommendation;

import org.springframework.lang.Nullable;

public interface RecommendationVerifier {
    boolean isRecommended(Long userId, Long shopInfoId, @Nullable String evidence);
}