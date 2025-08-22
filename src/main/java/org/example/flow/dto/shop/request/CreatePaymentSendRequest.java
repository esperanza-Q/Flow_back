package org.example.flow.dto.shop.request;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentSendRequest(
        @NotNull Long userId
) {}
