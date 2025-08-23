package org.example.flow.dto.funding.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedPopupResponseDTO {
    private Long fundingId;
    private String nickname;
    private Integer seeds;
}
