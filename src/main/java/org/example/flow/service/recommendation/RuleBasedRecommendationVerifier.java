package org.example.flow.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;

@Service
@Profile({"prod", "dev"}) // 필요에 맞게
@RequiredArgsConstructor
public class RuleBasedRecommendationVerifier implements RecommendationVerifier {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isRecommended(Long userId, Long shopInfoId, @Nullable String evidence) {
        final String sql = """
            SELECT (pc.status = 'ACCEPT') AS ok
            FROM payment_check pc
            WHERE pc.user_id = ? AND pc.shop_info_id = ?
            ORDER BY pc.created_at DESC
            LIMIT 1
        """;

        Boolean ok = jdbcTemplate.query(sql, ps -> {
            ps.setLong(1, userId);
            ps.setLong(2, shopInfoId);
        }, rs -> rs.next() && rs.getBoolean("ok"));

        // 해당 유저×매장 결제 이력이 전혀 없으면 false
        return Boolean.TRUE.equals(ok);
    }
}
