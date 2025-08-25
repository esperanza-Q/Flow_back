package org.example.flow.dto.shopMypage.response;

import com.fasterxml.jackson.annotation.JsonInclude;




@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentCheckResponse (
    Long paymentCheckId,
    Long userId,
    Long shopInfoId,
    long amount,
    String status,              // "ACCEPT"
    String createdAt,           // ISO-8601 (UTC, ...Z)

    boolean matched,
    Integer paymentPoints,
    Long nextRecommendShopId,   // 보상 실패면 null

    boolean counted,
    int countThisWeek,
    boolean awarded50,
    boolean awarded100
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long paymentCheckId;
        private Long userId;
        private Long shopInfoId;
        private long amount;
        private String status;
        private String createdAt;
        private boolean matched;
        private Integer paymentPoints;
        private Long nextRecommendShopId;
        private boolean counted;
        private int countThisWeek;
        private boolean awarded50;
        private boolean awarded100;

        public Builder paymentCheckId(Long v) {
            this.paymentCheckId = v;
            return this;
        }

        public Builder userId(Long v) {
            this.userId = v;
            return this;
        }

        public Builder shopInfoId(Long v) {
            this.shopInfoId = v;
            return this;
        }

        public Builder amount(long v) {
            this.amount = v;
            return this;
        }

        public Builder status(String v) {
            this.status = v;
            return this;
        }

        public Builder createdAt(String v) {
            this.createdAt = v;
            return this;
        }

        public Builder matched(boolean v) {
            this.matched = v;
            return this;
        }

        public Builder paymentPoints(int v)     {
            this.paymentPoints = Integer.valueOf(v);
            return this;
        }


        public Builder nextRecommendShopId(Long v) {
            this.nextRecommendShopId = v;
            return this;
        }

        public Builder counted(boolean v) {
            this.counted = v;
            return this;
        }

        public Builder countThisWeek(int v) {
            this.countThisWeek = v;
            return this;
        }

        public Builder awarded50(boolean v) {
            this.awarded50 = v;
            return this;
        }

        public Builder awarded100(boolean v) {
            this.awarded100 = v;
            return this;
        }

        public PaymentCheckResponse build() {
            return new PaymentCheckResponse(
                    paymentCheckId, userId, shopInfoId, amount, status, createdAt,
                    matched, paymentPoints, nextRecommendShopId,
                    counted, countThisWeek, awarded50, awarded100
            );
        }
    }
}
