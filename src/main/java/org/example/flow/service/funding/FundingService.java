package org.example.flow.service.funding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flow.dto.funding.request.SeedGiveRequestDTO;
import org.example.flow.dto.funding.response.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if(dto.getFundedSeeds()>=300){
            profile.setSeeds(profile.getPoint()+30);
        }

        SeedGiveResponseDTO responseDTO = SeedGiveResponseDTO.builder()
                .message("성공적으로 펀딩을 완료했습니다")
//                .first(first)
                .build();

//        if(first){
//
//        }

        return responseDTO;
    }

    public List<MyFundingResponseDTO> getMyFunding(){
        User user = SecurityUtil.getCurrentUser();

        // 특정 유저가 참여한 Funded 리스트
        List<Funded> fundeds = fundedRepository.findByUser(user);

// 같은 funding 기준으로 묶고, seed 합계 구하기
        Map<Funding, Integer> fundingSeedMap = fundeds.stream()
                .collect(Collectors.groupingBy(
                        Funded::getFunding,                           // key = funding
                        Collectors.summingInt(Funded::getSeeds)        // value = seed 합계
                ));

// DTO 리스트 빌드
        List<MyFundingResponseDTO> responses = fundingSeedMap.entrySet().stream()
                .map(entry -> {
                    Funding funding = entry.getKey();
                    Integer mySeed = entry.getValue();

                    return MyFundingResponseDTO.builder()
                            .fundingId(funding.getFundingId())
                            .title(funding.getTitle())
                            .organizer(funding.getOrganizer())
                            .goalSeed(funding.getGoalSeed())
                            .nowSeed(funding.getNowSeed())
                            .mySeed(mySeed)                         // ✅ 유저별 합계 seed
                            .endDate(funding.getEndDate())
                            .image(funding.getImage())
                            .status(funding.getStatus())
                            .build();
                })
                .toList();

        return responses;
    }
}