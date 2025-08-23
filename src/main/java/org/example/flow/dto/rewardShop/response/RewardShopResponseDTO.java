package org.example.flow.dto.rewardShop.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardShopResponseDTO {
    private Long rewardCouponId;  // 쿠폰아이디
    private String name;            // 쿠폰이름
    private String image;           // 쿠폰이미지
    private Integer amount;          // 쿠폰가격
    private String shopName;        // 쿠폰매장이름
}