package org.example.flow.service.recommendation;

import org.springframework.lang.Nullable; // ← 요걸 권장 (micrometer 아님)

public interface RecommendationVerifier {
    boolean isRecommended(Long userId, Long shopInfoId, @Nullable String evidence);
}