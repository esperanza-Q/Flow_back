package org.example.flow.dto.shop.response;

public record PaymentSendResponse(
        Long paymentCheckId,
        Long userId,
        Long shopInfoId,
        Integer amount,
        String status,          // "WAITING" | "ACCEPT" | "REJECT"
        String createdAt        // ISO-8601 UTC (Z)
) {}
