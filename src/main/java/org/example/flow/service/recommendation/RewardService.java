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
    private final WeeklyAiVisitCounterService weeklyAiVisitCounterService;

    public RewardService(
            ProfileRepository profileRepository,
            @Qualifier("acceptPaymentVerifier") RecommendationVerifier recommendationVerifier,
            WeeklyAiVisitCounterService weeklyAiVisitCounterService
    ) {
        this.profileRepository = profileRepository;
        this.recommendationVerifier = recommendationVerifier;
        this.weeklyAiVisitCounterService = weeklyAiVisitCounterService;
    }

    /* ───────── ✅ 통합: 결제(슬라이딩) + 주간 방문 누적(3/5회) ─────────
       - 추천 일치(matched)일 때만 결제 보상/주간 카운트 진행
       - 반환: matched, aiPaymentRewarded, paymentPoints, counted, countThisWeek, awarded50, awarded100
    */


    /** ✅ 결제 슬라이딩 보상: 적립된 포인트 수를 반환 (0이면 미지급) */
    public int awardAiPaymentPoints(Long userId, long paidAmount) {
        int pts = calcAiPaymentPoints(paidAmount);
        if (pts > 0) addPoint(userId, pts, "AI 추천매장 결제 리워드(슬라이딩)");
        return pts;
    }

    /** ✅ 체크인 주간 카운트/임계 보상: matched일 때만 호출 (count+임계 3/5 보상) */
    public WeeklyVisitAwardResult increaseCountAndAwardThresholds(Long userId) {
        int count = weeklyAiVisitCounterService.increaseAndGetCountThisWeek(userId);
        boolean awarded50 = false, awarded100 = false;
        if (count == 3) { addPoint(userId, 50,  "주간 방문 누적(3회)"); awarded50  = true; }
        if (count == 5) { addPoint(userId, 100, "주간 방문 누적(5회)"); awarded100 = true; }
        return new WeeklyVisitAwardResult(count, true, awarded50, awarded100);
    }

    /* ───────── ♻️ 펀딩 참여 리워드 (최종 규칙) ─────────
       - contributedPoints >= 300 → +30P
    */
    public boolean awardFundingOnParticipate(Long userId, int contributedPoints) {
        if (contributedPoints < 300) return false;
        addPoint(userId, 30, "펀딩 참여 리워드(300P 이상)");
        return true;
    }

    /* ───────── (호환) 기존 메서드들: 통합 메서드에 위임 ───────── */

    /** 결제 슬라이딩만: matched가 true이고 amount>0일 때만 포인트 지급 */
    public boolean awardAiRecommendedPayment(Long userId, Long shopInfoId, long paidAmount, boolean matched) {
        if (!matched) return false;          // 방문 일치가 선행 조건
        int paymentPoints = calcAiPaymentPoints(paidAmount);
        if (paymentPoints <= 0) return false; // 0점이면 '지급 안 됨'으로 간주
        addPoint(userId, paymentPoints, "AI 추천매장 결제 리워드(슬라이딩)");
        return true;
    }

    /** 체크인 시 주간 카운트/임계 보상만: matched가 true일 때만 */
    public WeeklyVisitAwardResult incrementWeeklyCountNoAward(Long userId) {
        int count = weeklyAiVisitCounterService.increaseAndGetCountThisWeek(userId);
        return new WeeklyVisitAwardResult(count, true, false, false);
    }

    // 🔧 슬라이딩 규칙: amount<=0 은 0점 (지급 없음)
    private int calcAiPaymentPoints(long amount) {
        if (amount <= 0)   return 0;   // ← 추가: 금액 미설정/0원은 미지급
        if (amount < 5_000)  return 20;
        if (amount < 10_000) return 40;
        if (amount < 20_000) return 60;
        return 80; // ≥ 20,000
    }

    private void addPoint(Long userId, int points, String reason) {
        Profile p = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));
        p.setPoint((p.getPoint() == null ? 0 : p.getPoint()) + points);
        // 로그 엔티티가 있다면 여기서 기록 추가
    }

    /* ───────── DTO 레코드들 ───────── */


    public record WeeklyVisitAwardResult(
            int countThisWeek,
            boolean counted,
            boolean awarded50,
            boolean awarded100
    ) {}
}

