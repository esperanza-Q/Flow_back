package org.example.flow.controller.shop;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.request.*;
import org.example.flow.dto.shop.response.*;
import org.example.flow.entity.PaymentCheck;
import org.example.flow.service.recommendation.PaymentConfirmService;
import org.example.flow.service.recommendation.RecommendFundingService;
import org.example.flow.service.shop.PaymentSendService;
import org.example.flow.service.shop.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.flow.service.shop.PlaceQueryService;
import org.example.flow.service.recommendation.RewardService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopPlaceController {

    private final PlaceQueryService placeQueryService;

    private final ShopService shopService;
    private final PaymentSendService paymentSendService;
    private final RewardService rewardService;
    private final PaymentConfirmService paymentConfirmService;
    private final RecommendFundingService recommendationFundingService;


    @GetMapping("/place/nearby")
    public List<PlaceNearbyResponse> getNearbyPlaces(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "5") int limit,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String category
    ) {
        if (limit <= 0 || limit > 50) limit = 5;
        return placeQueryService.getNearby(lat, lng, category, radius, limit);
    }

    @GetMapping("/{shopInfoId}")
    public ShopDetailResponse getShopDetail(@PathVariable Long shopInfoId) {
        return shopService.getShopDetail(shopInfoId);
    }

    /**
     * 💳 + 📆 통합 보상 엔드포인트
     *  - (0) payment_check 생성
     *  - (1) 결제 확정(슬라이딩 보상) → 성공 시 nextRecommendShopId 포함
     *  - (2) 체크인(추천이면 주간 카운트 + 3/5회 보상)
     */
    @PostMapping("/{shopInfoId}/reward")
    public ResponseEntity<PaymentCheckInitResponse> initPaymentCheck(
            @PathVariable Long shopInfoId,
            @RequestBody @Valid PaymentInitRequest req
    ) {
        PaymentCheck pc = paymentSendService.createPaymentCheck(shopInfoId, req.userId());

        String createdAtStr = PaymentSendService.toUtcString(pc.getCreatedAt());
        var body = PaymentCheckInitResponse.of(
                pc.getPaymentCheckId(),
                pc.getUser().getUserId(),
                pc.getShopInfo().getShopInfoId(),
                createdAtStr,
                pc.getStatus().name() // "WAITING"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

//    // 💳 결제 확정 → 슬라이딩 지급
//    // 💳 결제 확정 → (1) AI 슬라이딩 지급 → (2) 방문소진 + 다음추천 생성
//    @PostMapping(
//            value = "/payment/confirm",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody @Valid PaymentConfirmRequest req) {
//        /* ───────── 권장(A): DTO에 금액/확정 여부가 있는 경우 ───────── */
//        Map<String, Object> result = paymentConfirmService.confirm(
//                req.userId(),
//                req.shopInfoId(),
//                req.amount()
//        );
//        return ResponseEntity.ok(result);
//    }
//
//
//    // 📆 체크인 → (추천이면) 주간 카운트 증가 & 3/5회 보상
//    @PostMapping("/shop/check-in")
//    public Map<String, Object> checkIn (@RequestBody @Valid CheckInRequest req){
//        var r = rewardService.onCheckInAiRecommendedShop(req.userId(), req.shopInfoId());
//        return Map.of(
//                    "counted", r.counted(),
//                    "countThisWeek", r.countThisWeek(),
//                    "awarded50", r.awarded50(),
//                    "awarded100", r.awarded100()
//            );
//        }

        // ♻️ 펀딩 참여
        @PostMapping("/funding/participate")
        public RecommendFundingService.FundingParticipateResponse participate(@RequestBody FundingParticipateRequest req) {
            return recommendationFundingService.participate(
                    req.userId(),
                    req.fundingId(),
                    req.contributedPoints()
            );
        }
    }

