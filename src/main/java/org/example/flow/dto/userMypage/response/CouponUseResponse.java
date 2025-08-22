package org.example.flow.dto.userMypage.response;

import lombok.*;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class CouponUseResponse {
        private Long receiveCouponId;
        private Long user_id;
        private String type;       // "REWARD" | "VISIT"
        private String receiveAt;  // ISO-8601 문자열
        private boolean used;
    }