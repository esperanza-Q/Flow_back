// org.example.flow.dto.shop.response.PaymentCheckInitResponse
package org.example.flow.dto.shop.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentCheckInitResponse(
        Long paymentCheckId,
        Long userId,
        Long shopInfoId,
        String createdAt,
        String status // "WAITING"
) {
    public static PaymentCheckInitResponse of(Long id, Long userId, Long shopInfoId, String createdAt, String status) {
        return new PaymentCheckInitResponse(id, userId, shopInfoId, createdAt, status);
    }
}
