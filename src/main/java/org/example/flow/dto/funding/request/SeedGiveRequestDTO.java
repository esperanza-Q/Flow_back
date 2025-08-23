package org.example.flow.dto.funding.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class SeedGiveRequestDTO {
    private Long fundingId;
    private Integer fundedSeeds;
}
