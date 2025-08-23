package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.RecommendVisitLog;
import org.example.flow.entity.User;
import org.example.flow.repository.RecommendVisitLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager; // ← 추가
import java.time.*;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyAiVisitCounterService {

    private final RecommendVisitLogRepository repo;
    private final EntityManager em; // ← 추가 (JPA 레퍼런스 주입)
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    /** 추천 매장 체크인 시 호출 → 이번 주 카운트를 +1 후 현재 카운트 반환 */
    public int increaseAndGetCountThisWeek(Long userId) {
        LocalDate monday = LocalDate.now(ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        var opt = repo.findByUser_UserIdAndWeekStart(userId, monday);
        if (opt.isPresent()) {
            var log = opt.get();
            log.setVisitCount((log.getVisitCount() == null ? 0 : log.getVisitCount()) + 1);
            return log.getVisitCount();
        }

        // 👇 여기서 실제 select 없이 프록시만 가져와 관계를 설정
        var userRef = em.getReference(User.class, userId);

        var log = new RecommendVisitLog();
        log.setUser(userRef);
        log.setWeekStart(monday);
        log.setVisitCount(1);
        repo.save(log);
        return 1;
    }
}
