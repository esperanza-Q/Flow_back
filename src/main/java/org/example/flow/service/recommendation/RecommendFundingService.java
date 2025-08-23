package org.example.flow.service.recommendation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("recommendationFundingService")
@RequiredArgsConstructor
public class RecommendFundingService {

    private final RewardService rewardService;
    private final EntityManager em;

    /**
     * ♻️ 펀딩 참여 처리 (최종 규칙)
     * - contributedPoints >= 300 이면 +30P
     * - 그 외 보상 없음
     */
    @Transactional
    public FundingParticipateResponse participate(Long userId, Long fundingId, int contributedPoints) {
        // 1) funded 테이블에 참여 기록 INSERT
        em.createNativeQuery("""
            INSERT INTO funded (user_id, funding_id, seeds, created_at)
            VALUES (?1, ?2, ?3, NOW())
        """)
                .setParameter(1, userId)
                .setParameter(2, fundingId)
                .setParameter(3, contributedPoints)
                .executeUpdate();

        // 2) 보상 지급 (300P 이상이면 +30P)
        boolean rewarded = rewardService.awardFundingOnParticipate(userId, contributedPoints);

        // 3) 누적 참여 횟수는 funded 테이블 카운트로
        Long totalCount = ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM funded WHERE user_id = ?1
        """)
                .setParameter(1, userId)
                .getSingleResult()).longValue();

        return new FundingParticipateResponse(rewarded, totalCount.intValue());
    }

    /** ✅ 컨트롤러가 참조하는 내부 DTO */
    public record FundingParticipateResponse(boolean fundingRewarded, int participationCount) {}
}
