package org.example.flow.service.recommendShop;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.*;
import org.example.flow.repository.PlaceRepository;
import org.example.flow.repository.RecommendShopRepository;
import org.example.flow.repository.RecommendVisitLogRepository;
import org.example.flow.repository.ShopInfoRepository;
import org.example.flow.service.WeatherService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopRecommendationService {

    private final ShopInfoRepository shopInfoRepository;
    private final RecommendShopRepository recommendShopRepository;
    private final RecommendVisitLogRepository visitLogRepository;
    private final WeatherService weatherService;
    private final PlaceRepository placeRepository;

    public ShopInfo recommendShop(User user, LocalDateTime now) {

        String weather = weatherService.getTodayWeather(); // 오늘 날씨 가져오기
        List<ShopInfo> allShops = shopInfoRepository.findAll();

        // 규칙 기반 추천
//        ShopInfo recommendedShop = allShops.stream()
//                .filter(shop -> {
//                    if ("Rain".equalsIgnoreCase(weather)) return shop.getExplanationTitle().contains("카페");
//                    if (now.getHour() >= 12 && now.getHour() <= 14) return shop.getExplanationTitle().contains("식당");
//                    if ((now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY)
//                            && "Clear".equalsIgnoreCase(weather)) return shop.getExplanationTitle().contains("야외");
//                    return true; // 조건 안 맞으면 전부 배제하지 않고 후보군 유지
//                })
//                .collect(Collectors.toList()) // 조건에 맞는 후보들을 모음
//                .stream()
//                .skip(new Random(user.getUserId()).nextInt(allShops.size())) // 👈 유저 ID 기반 랜덤 시드
//                .findFirst()
//                .orElse(allShops.get(0));

        List<ShopInfo> candidates = allShops.stream()
                .filter(shop -> {
                    Place place = placeRepository.findByShopInfo(shop);
                    Place.Category category = place.getCategory();

                    if ("Clear".equalsIgnoreCase(weather)) {
                        // 주말 + 맑음 → FASHION, LIFE
                        if (now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            return category == Place.Category.FASHION || category == Place.Category.LIFE || category == Place.Category.CAFE;
                        }
                        // 평일 + 맑음 → CAFE
                        return category == Place.Category.CAFE;
                    }

                    if (now.getHour() >= 12 && now.getHour() <= 14) {
                        // 점심시간 → FOOD
                        return category == Place.Category.FOOD || category == Place.Category.CAFE;
                    }

                    if ("Rain".equalsIgnoreCase(weather)||"Clouds".equalsIgnoreCase(weather)) {
                        // 비 → ECT
                        return category == Place.Category.ECT || category == Place.Category.CAFE;
                    }

                    // 기타 상황 → 모든 후보 허용
                    return true;
                })
                .collect(Collectors.toList());

// 후보군 중 랜덤 선택 (유저 ID 기반 시드)
//        ShopInfo recommendedShop = candidates.stream()
//                .skip(new Random(user.getUserId()).nextInt(candidates.size()))
//                .findFirst()
//                .orElse(allShops.get(0));
        ShopInfo recommendedShop;
        if (candidates.isEmpty()) {
            recommendedShop = allShops.get(0); // fallback
        } else {
            recommendedShop = candidates.stream()
                    .skip(new Random().nextInt(candidates.size())) // 👈 매번 다른 랜덤값
                    .findFirst()
                    .orElse(allShops.get(0));
        }

        // 추천 기록 저장
        RecommendShop recommendShop = RecommendShop.builder()
                .user(user)
                .visited(false)
                .shopInfo(recommendedShop)
                .recommendInfo("규칙 기반 추천: 날씨=" + weather + ", 시간=" + now.getHour())
                .build();
        recommendShopRepository.save(recommendShop);

        // 주 단위 방문 로그 업데이트
//        LocalDate weekStart = now.with(DayOfWeek.MONDAY).toLocalDate();
//        RecommendVisitLog visitLog = visitLogRepository.findByUserAndWeekStart(user, weekStart)
//                .orElseGet(() -> new RecommendVisitLog(null, user, weekStart, 0));
//
//        visitLog.setVisitCount(visitLog.getVisitCount() + 1);
//        visitLogRepository.save(visitLog);

        return recommendedShop;
    }
}

