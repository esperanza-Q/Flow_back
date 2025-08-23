package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Consumer; // ✅ 이거!

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyVisitRewardService {

    // ... 레포지토리들 주입

    private static final int WEEKLY_VISIT_POINT = 30;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    // A안(매장별): (userId, shopInfoId, Consumer<Integer>)
    public boolean awardWeeklyVisitIfFirst(Long userId, Long shopInfoId, Consumer<Integer> pointAdder) {
        LocalDate monday = LocalDate.now(ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // ... (조회/생성 로직)
        // 최초 1회일 때만:
        pointAdder.accept(WEEKLY_VISIT_POINT);
        return true;
    }

    // B안(매장무관): (userId, Consumer<Integer>)
    public boolean awardWeeklyVisitIfFirst(Long userId, Consumer<Integer> pointAdder) {
        LocalDate monday = LocalDate.now(ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // ... (조회/생성 로직)
        // 최초 1회일 때만:
        pointAdder.accept(WEEKLY_VISIT_POINT);
        return true;
    }
}
