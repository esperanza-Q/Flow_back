package org.example.flow.controller.shop;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.request.CreatePaymentSendRequest;
import org.example.flow.dto.shop.response.PaymentSendResponse;
import org.example.flow.dto.shop.response.PlaceNearbyResponse;
import org.example.flow.dto.shop.response.PlaceSimpleResponse;
import org.example.flow.dto.shop.response.ShopDetailResponse;
import org.example.flow.service.shop.PaymentSendService;
import org.example.flow.service.shop.ShopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.flow.service.shop.PlaceQueryService;

import java.util.List;


@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopPlaceController {

    private final PlaceQueryService placeQueryService;

    private final ShopService shopService;
    private final PaymentSendService paymentSendService;

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

}

