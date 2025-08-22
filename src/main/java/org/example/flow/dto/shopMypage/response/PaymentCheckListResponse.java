package org.example.flow.dto.shopMypage.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentCheckListResponse {
    private List<Item> content;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Item {
        private Long paymentCheckId;
        private Long userId;
        private Long shopInfoId;
        private Integer amount;           // null 가능
        private String status;            // "WAITING" | "ACCEPT" | "REJECT"
        private LocalDateTime createdAt;  // ISO 포맷으로 직렬화
    }
}
