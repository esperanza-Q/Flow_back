package org.example.flow.controller.shopMypage;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shopMypage.request.AcceptPaymentRequest;
import org.example.flow.dto.shopMypage.request.UpdateShopInfoRequest;
import org.example.flow.dto.shopMypage.response.*;
import org.example.flow.service.shopMypage.ShopMypageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/shopMypage")
@RequiredArgsConstructor
public class ShopMypageController {

    private final ShopMypageService shopMypageService;

    @GetMapping("/summaryShopInfo")
    public SummaryShopInfoResponse getSummaryShopInfo(@RequestParam Long shopInfoId) {
        return shopMypageService.getSummaryShopInfo(shopInfoId);
    }

    @GetMapping("/updateShopInfo")
    public UpdateShopInfoResponse getUpdateShopInfo(@RequestParam Long shopInfoId) {
        return shopMypageService.getUpdateShopInfo(shopInfoId);
    }

    @PostMapping("/updateShopInfo")
    public UpdateShopInfoResponse updateShopInfo(
            @RequestParam Long shopInfoId,
            @RequestBody UpdateShopInfoRequest request
    ) {
        return shopMypageService.updateShopInfo(shopInfoId, request);
    }
    @GetMapping("/paymentCheck")
    public PaymentCheckListResponse getPaymentChecks(
            @RequestParam String status,                 // WAITING | ACCEPT | REJECT
            @RequestParam Long shopInfoId,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sort
    ) {
        return shopMypageService.getPaymentChecks(shopInfoId, status, sort);
    }

    @PostMapping("/paymentCheck/{id}/accept")
    public PaymentCheckResponse acceptPaymentCheck(
            @PathVariable("id") Long paymentCheckId,
            @RequestBody AcceptPaymentRequest request
    ) {
        return shopMypageService.acceptPaymentCheck(paymentCheckId, request);
    }

    @PostMapping(value = "/addrewardCoupon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RewardCouponResponse addRewardCoupon(
            @RequestParam("name") String name,
            @RequestParam("amount") Integer amount,
            @RequestParam("shopInfoId") Long shopInfoId,
            @RequestPart("image") MultipartFile image
    ) {
        return shopMypageService.addRewardCoupon(shopInfoId, name, amount, image);
    }

}
