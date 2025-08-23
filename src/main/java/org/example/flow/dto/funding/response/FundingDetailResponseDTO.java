package org.example.flow.dto.funding.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class FundingDetailResponseDTO {
    private Long fundingId;
    private String title;
    private String organizer;
    private Integer goalSeed;
    private Integer nowSeed;
    private LocalDate endDate;       // LocalDate를 문자열로 변환해서 반환
    private Long participants; // 참여자 수
    private String image;
    private String introduction;
}
