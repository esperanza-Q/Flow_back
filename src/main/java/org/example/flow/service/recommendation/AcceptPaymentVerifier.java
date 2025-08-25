package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("acceptPaymentVerifier")
@RequiredArgsConstructor
public class AcceptPaymentVerifier implements RecommendationVerifier {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isRecommended(Long userId, Long shopInfoId, @org.springframework.lang.Nullable String ignore) {
        // 최신 결제 상태가 ACCEPT 인지 확인
        final String sql = """
            SELECT CASE WHEN status = 'ACCEPT' THEN 1 ELSE 0 END AS ok
            FROM payment_check
            WHERE user_id = ? AND shop_info_id = ?
            ORDER BY created_at DESC
            LIMIT 1
        """;
        Boolean ok = jdbcTemplate.query(sql, ps -> {
            ps.setLong(1, userId);
            ps.setLong(2, shopInfoId);
        }, rs -> rs.next() ? rs.getInt("ok") == 1 : false);

        return Boolean.TRUE.equals(ok);
    }
}
