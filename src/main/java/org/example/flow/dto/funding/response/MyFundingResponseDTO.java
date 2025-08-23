package org.example.flow.dto.funding.response;

import lombok.*;
import org.example.flow.entity.Funding;

import java.time.LocalDate;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyFundingResponseDTO {

    private Long fundingId;      // 펀딩 아이디
    private String title;        // 펀딩 제목
    private String organizer;    // 주최지
    private Integer goalSeed;        // 목표 씨앗 수
    private Integer nowSeed;         // 현재 모인 씨앗 수
    private Integer mySeed;          // 내가 펀딩한 씨앗 수
    private LocalDate endDate;      // 마감 날짜 (예: 2025-10-10)
    private String image;        // 이미지 URL
    private Funding.STATUS status;       // INPROGRESS, FINISHED
}
