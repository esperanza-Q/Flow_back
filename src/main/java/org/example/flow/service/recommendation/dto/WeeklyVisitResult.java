package org.example.flow.service.recommendation.dto;


public record WeeklyVisitResult(
        boolean counted,     // 이번 호출에서 +1이 실제 반영됐는가
        int countThisWeek,   // 증가 후 이번 주 누적
        boolean awarded50,   // 3회 달성
        boolean awarded100   // 5회 달성
) {}