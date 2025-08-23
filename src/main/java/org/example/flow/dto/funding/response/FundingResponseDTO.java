package org.example.flow.dto.funding.response;

import lombok.*;
import org.example.flow.entity.Funding;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FundingResponseDTO {

    private String nickname;
    private List<FundingInfo> fundingList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FundingInfo {
        private Long fundingId;
        private String title;
        private String organizer;
        private Integer goalSeed;
        private Integer nowSeed;
        private LocalDate endDate;
        private String image;
        private Funding.CATEGORY category;
    }
}