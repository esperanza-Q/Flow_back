package org.example.flow.controller.shop;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.request.CreatePaymentSendRequest;
import org.example.flow.dto.shop.request.FundingParticipateRequest;
import org.example.flow.dto.shop.request.CheckInRequest;
import org.example.flow.dto.shop.request.PaymentConfirmRequest;
import org.example.flow.dto.shop.response.PaymentSendResponse;
import org.example.flow.dto.shop.response.PlaceNearbyResponse;
import org.example.flow.dto.shop.response.PlaceSimpleResponse;
import org.example.flow.dto.shop.response.ShopDetailResponse;
import org.example.flow.service.recommendation.PaymentConfirmService;
import org.example.flow.service.recommendation.RecommendFundingService;
import org.example.flow.service.shop.PaymentSendService;
import org.example.flow.service.shop.ShopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.flow.service.shop.PlaceQueryService;
import org.example.flow.service.recommendation.RewardService;

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

    // GET /api/shop/place?category=FOOD&page=0&size=20&sort=placeId,desc
    @GetMapping("/place")
    public Page<PlaceSimpleResponse> getPlaces(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20, sort = "placeId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return placeQueryService.getPlaces(category, pageable);
    }

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

    @PostMapping("/{shopInfoId}/reward")
    public ResponseEntity<PaymentSendResponse> create(
            @PathVariable Long shopInfoId,
            @RequestBody @Valid CreatePaymentSendRequest req
    ) {
        PaymentSendResponse res = paymentSendService.createPaymentCheck(shopInfoId, req.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 💳 결제 확정 → 슬라이딩 지급
    @PostMapping(
            value = "/payment/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> confirmPayment(@RequestBody @Valid PaymentConfirmRequest req) {
        // 콘솔에서 어떤 Verifier가 물렸는지 확인용
        return paymentConfirmService.confirm(req.userId(), req.shopInfoId());
    }

    // 📆 체크인 → (추천이면) 주간 카운트 증가 & 3/5회 보상
    @PostMapping("/shop/check-in")
    public Map<String, Object> checkIn (@RequestBody @Valid CheckInRequest req){
        var r = rewardService.onCheckInAiRecommendedShop(req.userId(), req.shopInfoId());
        return Map.of(
                    "counted", r.counted(),
                    "countThisWeek", r.countThisWeek(),
                    "awarded50", r.awarded50(),
                    "awarded100", r.awarded100()
            );
        }

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

