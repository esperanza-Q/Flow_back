package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service("acceptPaymentVerifier")
@RequiredArgsConstructor
public class AcceptPaymentVerifier implements RecommendationVerifier {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isRecommended(Long userId, Long shopInfoId, @Nullable String ignored) {
        final String sql = """
            SELECT 1
            FROM payment_check pc
            WHERE pc.user_id = ? 
              AND pc.shop_info_id = ?
              AND pc.status = 'ACCEPT'
            ORDER BY pc.created_at DESC
            LIMIT 1
        """;
        Boolean ok = jdbcTemplate.query(sql, ps -> {
            ps.setLong(1, userId);
            ps.setLong(2, shopInfoId);
        }, rs -> rs.next() ? Boolean.TRUE : Boolean.FALSE);
        return Boolean.TRUE.equals(ok);
    }
}

