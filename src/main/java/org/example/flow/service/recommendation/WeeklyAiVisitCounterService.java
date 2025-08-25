// org.example.flow.service.recommendation.WeeklyAiVisitCounterService
package org.example.flow.service.recommendation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.flow.entity.RecommendVisitLog;
import org.example.flow.entity.User;
import org.example.flow.repository.RecommendVisitLogRepository;
import org.example.flow.service.recommendation.dto.WeeklyVisitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;



@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyAiVisitCounterService {


    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final RecommendVisitLogRepository repo;
    private final EntityManager em;

    /** 이번 주 월요일(주차 시작일) 계산 */

    private LocalDate weekStartKst() {
        return LocalDate.now(ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /** ✅ 네가 원래 쓰던 시그니처: +1 하고 이번 주 누적 반환 (정수만) */
    public int increaseAndGetCountThisWeek(Long userId) {
        LocalDate monday = weekStartKst();

        var opt = repo.findByUser_UserIdAndWeekStart(userId, monday);
        if (opt.isPresent()) {
            var log = opt.get();
            Integer cur = log.getVisitCount();
            log.setVisitCount((cur == null ? 0 : cur) + 1);
            // JPA가 dirty check로 update
            return log.getVisitCount();
        }

        // select 없이 프록시만 물려서 관계 세팅
        var userRef = em.getReference(User.class, userId);
        var log = new RecommendVisitLog();
        log.setUser(userRef);
        log.setWeekStart(monday);
        log.setVisitCount(1);
        repo.save(log);
        return 1;
    }

    /** ✅ 확장판: +1 한 뒤 임계(3/5회) 보상 플래그까지 리턴 */
    public WeeklyVisitResult incrementAndReport(Long userId) {
        int count = increaseAndGetCountThisWeek(userId);
        boolean awarded50 = (count == 3);
        boolean awarded100 = (count == 5);
        return new WeeklyVisitResult(true, count, awarded50, awarded100);


    }

}
