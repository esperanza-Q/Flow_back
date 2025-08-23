package org.example.flow.dto.shopMypage.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardCouponResponse {
    private Long rewardCouponId;
    private Long shopInfoId;
    private String name;
    private Integer amount;
    private String image; // 예: "101.png"
}
