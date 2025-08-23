package org.example.flow.controller.rewardShop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.rewardShop.response.RewardShopResponseDTO;
import org.example.flow.service.rewardShop.RewardShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rewardShop")
@RequiredArgsConstructor
public class RewardShopController {

    private final RewardShopService rewardShopService;

    @GetMapping("")
    public List<RewardShopResponseDTO> getRewardCoupons() {

        return rewardShopService.getRewardCoupons();
    }

    @GetMapping("/receive")
    public ResponseEntity<?> receiveRewardCoupon(@RequestParam Long rewardCouponId) throws Exception {

        rewardShopService.receiveRewardCoupon(rewardCouponId);

        return ResponseEntity.ok("쿠폰 지급 완료!");
    }

}
