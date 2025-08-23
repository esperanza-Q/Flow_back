package org.example.flow.repository;

import org.example.flow.entity.RecommendVisitLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//주간 누적용
public interface RecommendVisitLogRepository extends JpaRepository<RecommendVisitLog, Long> {
    Optional<RecommendVisitLog> findByUser_UserIdAndWeekStart(Long userId, LocalDate weekStart);

}