package org.example.flow.dto.shop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FundingParticipateRequest(
        @NotNull Long userId,
        @NotNull Long fundingId,
        @Min(1) int contributedPoints
) {}
