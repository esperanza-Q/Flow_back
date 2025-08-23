package org.example.flow.dto.shop.request;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull Long userId,
        @NotNull Long shopInfoId
) {}
