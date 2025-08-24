package org.example.flow.service.user;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.user.request.BusinessHoursDTO;
import org.example.flow.dto.user.request.ShopInfoWriteRequestDTO;
import org.example.flow.entity.*;
import org.example.flow.repository.BenefitReqRepository;
import org.example.flow.repository.BusinessHoursRepository;
import org.example.flow.repository.ShopImageRepository;
import org.example.flow.repository.ShopInfoRepository;
import org.example.flow.security.SecurityUtil;
import org.example.flow.service.CommentSummaryService;
import org.example.flow.service.GooglePlacesService;
import org.example.flow.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopInfoService {

    private final ShopInfoRepository shopInfoRepository;
    private final S3Service s3Service;
    private final BenefitReqRepository benefitReqRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final SecurityUtil securityUtil;
    private final ShopImageRepository shopImageRepository;
    private final CommentSummaryService commentSummaryService;
    private final GooglePlacesService googlePlacesService;

    @Transactional
    public void saveShopInfo(ShopInfoWriteRequestDTO dto) throws IOException {
        User user = securityUtil.getCurrentUser();

        // 1. 기존 ShopInfo 조회
        ShopInfo shopInfo = shopInfoRepository.findById(dto.getShopInfoId())
                .orElseThrow(() -> new RuntimeException("ShopInfo not found"));

        // 2. 권한 체크
        if (!shopInfo.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("권한이 없습니다.");
        }

        try {
            // 3. 이미지 업로드
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                List<String> uploadedImageUrls = new ArrayList<>();
                for (MultipartFile file : dto.getImages()) {
                    if (file != null && !file.isEmpty()) {
                        String url = s3Service.upload(file);
                        uploadedImageUrls.add(url);

                        // ShopImage 엔티티 생성 및 저장
                        ShopImage shopImage = new ShopImage();
                        shopImage.setShopInfo(shopInfo);  // FK 설정
                        shopImage.setImage(url);
                        shopImageRepository.save(shopImage);  // DB 저장
                    }

                }
                // 엔티티에는 아직 컬럼이 없으므로, 필요하면 imageUrls 컬럼 추가 후 set
//                 shopInfo.setImageUrls(uploadedImageUrls);
            }

            // 4. 씨앗 적립 조건
            if (dto.getSeedCondition()) {
                BenefitReq benefitReq = BenefitReq.builder()
                        .shopInfo(shopInfo)
                        .reqName(BenefitReq.ReqName.SEED)
                        .seedDetail(dto.getSeedDetail())
                        .build();

                benefitReqRepository.save(benefitReq);
            }

            // 5. 방문 시 쿠폰 조건
            if (dto.getCouponCondition()) {
                String couponUrl = s3Service.upload(dto.getCouponImage());
                BenefitReq benefitReq = BenefitReq.builder()
                        .shopInfo(shopInfo)
                        .reqName(BenefitReq.ReqName.COUPON)
                        .visitCount(dto.getVisitCount())
                        .couponType(dto.getCouponType())
                        .couponImage(couponUrl)
                        .build();

                benefitReqRepository.save(benefitReq);
            }

            // 6. 나머지 필드 업데이트
            if (dto.getExplanationTitle() != null) shopInfo.setExplanationTitle(dto.getExplanationTitle());
            if (dto.getExplanationContent() != null) shopInfo.setExplanationContent(dto.getExplanationContent());

            // 7. BusinessHours 처리
            if (dto.getBusinessHours() != null && !dto.getBusinessHours().isEmpty()) {
                // 기존 BusinessHours 삭제
                List<BusinessHours> existingHours = businessHoursRepository.findByShopInfo(shopInfo);
                businessHoursRepository.deleteAll(existingHours);

                // 새로 등록
                List<BusinessHours> newHours = new ArrayList<>();
                for (BusinessHoursDTO bhDto : dto.getBusinessHours()) {
                    BusinessHours bh = BusinessHours.builder()
                            .shopInfo(shopInfo)
                            .week(bhDto.getWeek())
                            .openTime(bhDto.getOpenTime())
                            .closeTime(bhDto.getCloseTime())
                            .build();
                    newHours.add(bh);
                }
                businessHoursRepository.saveAll(newHours);
            }


            // 변경 감지로 자동 저장
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    //OPENAI 리뷰
    @Transactional
    public ShopInfo updateShopInfoWithGoogleReviews(Long shopInfoId, String placeId) throws Exception {
        // 1. ShopInfo 조회
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // 2. Google Places API 호출 → 리뷰 가져오기
        List<String> reviews = googlePlacesService.fetchReviews(placeId);

        // 3. 추천 코멘트 생성 및 ShopInfo에 저장
        shopInfo = commentSummaryService.generateAndSetComments(shopInfo, reviews);

        // 4. DB 저장
        return shopInfoRepository.save(shopInfo);
    }

}