package org.example.flow.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecommendationJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void insert(Long userId, Long shopInfoId) {
        String sql = "INSERT INTO ai_recommendation (user_id, shop_info_id, valid_until) " +
                "VALUES (?, ?, TIMESTAMPADD(DAY, 7, NOW()))";
        jdbcTemplate.update(sql, userId, shopInfoId);
    }
}
