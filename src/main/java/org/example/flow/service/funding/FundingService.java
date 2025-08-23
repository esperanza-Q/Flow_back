package org.example.flow.service.funding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flow.dto.funding.request.SeedGiveRequestDTO;
import org.example.flow.dto.funding.response.FundingDetailResponseDTO;
import org.example.flow.dto.funding.response.FundingResponseDTO;
import org.example.flow.dto.funding.response.SeedGiveResponseDTO;
import org.example.flow.dto.funding.response.SeedPopupResponseDTO;
import org.example.flow.entity.Funded;
import org.example.flow.entity.Funding;
import org.example.flow.entity.Profile;
import org.example.flow.entity.User;
import org.example.flow.repository.FundedRepository;
import org.example.flow.repository.FundingRepository;
import org.example.flow.repository.ProfileRepository;
import org.example.flow.repository.UserRepository;
import org.example.flow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingService {
    private final UserRepository userRepository;
    private final FundingRepository fundingRepository;
    private final FundedRepository fundedRepository;
    private final ProfileRepository profileRepository;

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

    public SeedPopupResponseDTO getSeedPopup(Long fundingId){
        User user = SecurityUtil.getCurrentUser();
        Profile profile = profileRepository.findByUser(user);
        Funding funding = fundingRepository.findByFundingId(fundingId);
        SeedPopupResponseDTO seedPopupResponseDTO = SeedPopupResponseDTO.builder()
                .fundingId(fundingId)
                .nickname(user.getNickname())
                .seeds(profile.getSeeds())
                .build();
        return seedPopupResponseDTO;
    }

    @Transactional
    public SeedGiveResponseDTO giveSeed(SeedGiveRequestDTO dto){
        Funding funding = fundingRepository.findByFundingId(dto.getFundingId());
        funding.setNowSeed(funding.getNowSeed()+dto.getFundedSeeds());
        User user = SecurityUtil.getCurrentUser();
        Profile profile = profileRepository.findByUser(user);
        profile.setSeeds(profile.getSeeds()-dto.getFundedSeeds());

        Boolean first = !(fundedRepository.existsFundedByUser(user));

        Funded funded = Funded.builder()
                .user(user)
                .funding(funding)
                .seeds(dto.getFundedSeeds())
                .build();

        fundedRepository.save(funded);

        if(funding.getNowSeed()>=funding.getGoalSeed()){
            funding.setStatus(Funding.STATUS.FINISHED);
        }

        SeedGiveResponseDTO responseDTO = SeedGiveResponseDTO.builder()
                .message("성공적으로 펀딩을 완료했습니다")
                .first(first)
                .build();

        return responseDTO;
    }

}
