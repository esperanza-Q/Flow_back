package org.example.flow.service.funding;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.funding.response.FundingDetailResponseDTO;
import org.example.flow.dto.funding.response.FundingResponseDTO;
import org.example.flow.entity.Funded;
import org.example.flow.entity.Funding;
import org.example.flow.entity.User;
import org.example.flow.repository.FundedRepository;
import org.example.flow.repository.FundingRepository;
import org.example.flow.repository.UserRepository;
import org.example.flow.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FundingService {
    private final UserRepository userRepository;
    private final FundingRepository fundingRepository;
    private final FundedRepository fundedRepository;

    public FundingResponseDTO getFunding() {
        User user = SecurityUtil.getCurrentUser(); // 현재 로그인 유저

        // INPROGRESS인 펀딩 조회
        List<Funding> fundings = fundingRepository.findByStatus(Funding.STATUS.INPROGRESS);

        // DTO 변환
        List<FundingResponseDTO.FundingInfo> fundingList = fundings.stream()
                .map(f -> FundingResponseDTO.FundingInfo.builder()
                        .fundingId(f.getFundingId())
                        .title(f.getTitle())
                        .organizer(f.getOrganizer())
                        .goalSeed(f.getGoalSeed())
                        .nowSeed(f.getNowSeed())
                        .endDate(f.getEndDate()) // 예시, 필요시 수정
                        .image(f.getImage())
                        .category(f.getCategory())
                        .build())
                .toList();

        return new FundingResponseDTO(user.getNickname(), fundingList);
    }

    public FundingDetailResponseDTO getFundingDetail(Long fundingId) {
//        User user = SecurityUtil.getCurrentUser();
        Funding funding = fundingRepository.findByFundingId(fundingId);
        Long participants = fundedRepository.findByFunding(funding)
                .stream()
                .map(Funded::getUser)
                .distinct() // 중복 제거
                .count();

        FundingDetailResponseDTO fundingDetailResponseDTO = FundingDetailResponseDTO.builder()
                .fundingId(fundingId)
                .title(funding.getTitle())
                .organizer(funding.getOrganizer())
                .goalSeed(funding.getGoalSeed())
                .nowSeed(funding.getNowSeed())
                .endDate(funding.getEndDate())
                .image(funding.getImage())
                .introduction(funding.getIntroduction())
                .participants(participants)
                .build();

        return fundingDetailResponseDTO;
    }

}
