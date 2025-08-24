// src/main/java/org/example/flow/service/home/HomeService.java
package org.example.flow.service.home;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.home.response.HomeResponse;
import org.example.flow.entity.*;
import org.example.flow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final ProfileRepository profileRepository;
    private final RecommendShopRepository recommendShopRepository;
    private final ShopImageRepository shopImageRepository;
    private final BusinessHoursRepository businessHoursRepository;

    public HomeResponse getHome(Long userId) {

        // 1) 프로필 → 닉네임/포인트/유저
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalStateException("Profile not found for userId=" + userId));
        User user = profile.getUser();

        // 2) 최근 추천 매장
        RecommendShop rec = recommendShopRepository
                .findTopByUser_UserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalStateException("No recent recommendation for userId=" + userId));

        ShopInfo shopInfo = rec.getShopInfo();

        // 3) 체크한 날짜 리스트 (User.paymentChecks 이용, status=ACCEPT만)
        List<String> checkDates = Optional.ofNullable(user.getPaymentChecks())
                .orElse(List.of())
                .stream()
                .filter(pc -> pc != null && pc.getStatus() != null && "ACCEPT".equals(pc.getStatus().name()))
                .sorted(Comparator.comparing(PaymentCheck::getCreatedAt)) // 오래→최근 정렬 (원하면 reversed()로 변경)
                .map(pc -> pc.getCreatedAt().toLocalDate().format(DateTimeFormatter.ISO_DATE))
                .collect(toList());

        // 4) 매장 이미지: 첫 번째 1장만
        String shopImageUrl = shopImageRepository.findByShopInfo_ShopInfoId(shopInfo.getShopInfoId())
                .stream()
                .findFirst()
                .map(ShopImage::getImage)
                .orElse(null);

        // 5) 오픈 여부: 오늘 요일의 영업시간 내인지 체크
        boolean open = isOpenNow(shopInfo);

        return HomeResponse.builder()
                .nickname(user.getNickname())
                .point(profile.getPoint())
                .checkDate(checkDates)
                .shopName(shopInfo.getExplanationTitle())
                .openStatus(open)
                .recommendShopId(rec.getRecommendShopId())
                .shopInfoId(shopInfo.getShopInfoId())
                .shopImage(shopImageUrl)
                .recommendInfo(rec.getRecommendInfo())
                .build();
    }

    private boolean isOpenNow(ShopInfo shopInfo) {

        List<BusinessHours> hoursList = businessHoursRepository.findByShopInfo(shopInfo);
        if (hoursList.isEmpty()) return false;

        String today = LocalDate.now().getDayOfWeek().name(); // MONDAY..SUNDAY
        LocalTime now = LocalTime.now();

        // 단순 개방시간<=now<마감시간
        return hoursList.stream().anyMatch(h ->
                h != null
                        && h.getWeek() != null
                        && today.equals(h.getWeek().name())
                        && h.getOpenTime() != null
                        && h.getCloseTime() != null
                        && ( !now.isBefore(h.getOpenTime()) && now.isBefore(h.getCloseTime()) )
        );
    }
}
