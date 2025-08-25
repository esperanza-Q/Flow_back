package org.example.flow.service.home;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.home.response.RecommendShopResponse;
import org.example.flow.entity.BusinessHours;
import org.example.flow.entity.RecommendShop;
import org.example.flow.entity.ShopInfo;
import org.example.flow.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeRecommendService {

    private final RecommendShopRepository recommendShopRepository;
    private final ShopImageRepository shopImageRepository;
    private final ReceiveRewardCouponRepository receiveRewardCouponRepository;
    private final ReceiveVisitCouponRepository receiveVisitCouponRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final VisitedRepository visitedRepository;

    public RecommendShopResponse getRecommendShop(Long recommendShopId) {
        RecommendShop rs = recommendShopRepository.findById(recommendShopId)
                .orElseThrow(() -> new RuntimeException("추천 매장이 존재하지 않습니다. id=" + recommendShopId));

        ShopInfo shopInfo = rs.getShopInfo();
        Long userId = rs.getUser().getUserId();
        Long shopInfoId = shopInfo.getShopInfoId();

        // 방문 날짜 리스트
        List<String> checkDates = new ArrayList<>();
         visitedRepository.findTop10ByUser_UserIdAndShopInfo_ShopInfoIdOrderByCreatedAtDesc(userId, shopInfoId)
                 .forEach(v -> checkDates.add(v.getCreatedAt().toLocalDate().format(DateTimeFormatter.ISO_DATE)));



        // 매장 대표 이미지
        String firstImageUrl = null;
        if (shopInfo != null && shopInfoId != null) {
            var images = shopImageRepository.findByShopInfo_ShopInfoId(shopInfoId);
            if (images != null && !images.isEmpty()) {
                firstImageUrl = images.get(0).getImage();
            }
        }

        // comment 리스트
        List<RecommendShopResponse.Comment> comments = new ArrayList<>();

        if (rs.getRecommendInfo() != null && !rs.getRecommendInfo().isBlank()) {
            for (String line : rs.getRecommendInfo().split("\\r?\\n")) {
                String trimmed = line.trim();
                if (!trimmed.isBlank()) comments.add(new RecommendShopResponse.Comment(trimmed));
            }
        }

        // 쿠폰 타입 판별 (보유 여부)
        boolean hasReward = !receiveRewardCouponRepository
                .findByUser_UserIdAndRewardCoupon_ShopInfo_ShopInfoId(userId, shopInfoId).isEmpty();
        boolean hasVisit = !receiveVisitCouponRepository
                .findByUser_UserIdAndBenefitReq_ShopInfo_ShopInfoId(userId, shopInfoId).isEmpty();
        String couponType = hasReward ? "REWARD" : (hasVisit ? "VISIT" : "NONE");

        // 실제 방문 카운트
        long visitCount = visitedRepository.countByUser_UserIdAndShopInfo_ShopInfoId(userId, shopInfoId);

        boolean open = isOpenNow(shopInfo);

        return RecommendShopResponse.builder()
                .nickname(rs.getUser().getNickname())
                .checkDate(checkDates)
                .shopName(shopInfo.getExplanationTitle())
                .openStatus(open)
                .shopInfoId(shopInfoId)
                .shopImage(firstImageUrl)
                .comments(comments)
                .recommendInfo(rs.getRecommendInfo())
                .visitCount((int) visitCount)
                .couponType(couponType)
                .build();
    }

    private boolean isOpenNow(ShopInfo shopInfo) {
        List<BusinessHours> hoursList = businessHoursRepository.findByShopInfo(shopInfo);
        if (hoursList.isEmpty()) return false;

        String today = LocalDate.now().getDayOfWeek().name(); // MONDAY..SUNDAY
        LocalTime now = LocalTime.now();

        return hoursList.stream().anyMatch(h ->
                h != null
                        && h.getWeek() != null
                        && today.equals(h.getWeek().name())
                        && h.getOpenTime() != null
                        && h.getCloseTime() != null
                        && (!now.isBefore(h.getOpenTime()) && now.isBefore(h.getCloseTime()))
        );
    }
}
