package org.example.flow.dto.shopMypage.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentCheckResponse {
    private Long paymentCheckId;
    private Long userId;
    private Long shopInfoId;
    private Integer amount;
    private String status;          // "ACCEPT"
    private LocalDateTime createdAt; // 예: 2025-08-21T01:23:45
}
