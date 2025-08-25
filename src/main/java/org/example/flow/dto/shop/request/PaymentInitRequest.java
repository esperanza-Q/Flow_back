// org.example.flow.dto.shop.request.PaymentInitRequest
package org.example.flow.dto.shop.request;

import jakarta.validation.constraints.NotNull;

public record PaymentInitRequest(
        @NotNull Long userId
) {}
