package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.Profile;
import org.example.flow.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RewardService {

    private final ProfileRepository profileRepository;
    private final RecommendationVerifier recommendationVerifier;
    private final WeeklyAiVisitCounterService weeklyAiVisitCounterService; // 아래 2) 참고

    public RewardService(
            ProfileRepository profileRepository,
            @Qualifier("acceptPaymentVerifier") RecommendationVerifier recommendationVerifier, // ← 여기!
            WeeklyAiVisitCounterService weeklyAiVisitCounterService
    ) {
        this.profileRepository = profileRepository;
        this.recommendationVerifier = recommendationVerifier;
        this.weeklyAiVisitCounterService = weeklyAiVisitCounterService;
    }

    /* ───────── 💳 AI 추천 매장 결제: 금액 구간 슬라이딩 ───────── */
    public boolean awardAiRecommendedPayment(Long userId, Long shopInfoId, long paidAmount) {
        if (!recommendationVerifier.isRecommended(userId, shopInfoId, null)) return false; // ✅ now=null 허용
        int pts = calcAiPaymentPoints(paidAmount);
        addPoint(userId, pts, "AI 추천매장 결제 리워드(슬라이딩)");
        return true;
    }

    private int calcAiPaymentPoints(long amount) {
        if (amount < 5_000)  return 20;
        if (amount < 10_000) return 40;
        if (amount < 20_000) return 60;
        return 80; // ≥ 20,000
    }

    /* ───────── 📆 주간 방문 누적: 추천매장 3/5회 도달 보상 ───────── */
    public WeeklyVisitAwardResult onCheckInAiRecommendedShop(Long userId, Long shopInfoId) {
        if (!recommendationVerifier.isRecommended(userId, shopInfoId, null)) {
            return new WeeklyVisitAwardResult(0, false, false, false);
        }
        int count = weeklyAiVisitCounterService.increaseAndGetCountThisWeek(userId);

        boolean awarded50 = false, awarded100 = false;
        if (count == 3) { addPoint(userId, 50, "주간 방문 누적(추천 3회)"); awarded50 = true; }
        if (count == 5) { addPoint(userId, 100, "주간 방문 누적(추천 5회)"); awarded100 = true; }

        return new WeeklyVisitAwardResult(count, true, awarded50, awarded100);
    }

    public record WeeklyVisitAwardResult(int countThisWeek, boolean counted, boolean awarded50, boolean awarded100) {}

    /* ───────── ♻️ 펀딩 참여 리워드 (최종 규칙) ─────────
       - contributedPoints >= 300 → +30P
    */
    public boolean awardFundingOnParticipate(Long userId, int contributedPoints) {
        if (contributedPoints < 300) return false;
        addPoint(userId, 30, "펀딩 참여 리워드(300P 이상)");
        return true;
    }

    /* ───────── 공통 포인트 적립 ───────── */
    private void addPoint(Long userId, int points, String reason) {
        Profile p = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));
        p.setPoint((p.getPoint() == null ? 0 : p.getPoint()) + points);
        // 로그 엔티티 없이 요구이므로 적립 이력은 생략
    }
}
