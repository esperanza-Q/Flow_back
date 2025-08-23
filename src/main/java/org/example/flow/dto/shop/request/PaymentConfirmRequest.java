package org.example.flow.dto.shop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentConfirmRequest(
        @NotNull Long userId,
        @NotNull Long shopInfoId,
        @Min(0) long amount
) {}
