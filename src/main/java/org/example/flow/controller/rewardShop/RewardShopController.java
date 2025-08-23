package org.example.flow.controller.rewardShop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.funding.response.MyFundingResponseDTO;
import org.example.flow.dto.rewardShop.response.RewardShopResponseDTO;
import org.example.flow.repository.RewardShopRepository;
import org.example.flow.security.SecurityUtil;
import org.example.flow.service.rewardShop.RewardShopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
