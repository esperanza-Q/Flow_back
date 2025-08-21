package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reward_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_coupon_id", nullable = false)
    private Long rewardCouponId;   // PK

    // FK: ShopInfo (한 매장에 여러 보상 쿠폰 가능 → N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false)
    private ShopInfo shopInfo;

    // 쿠폰 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 가격
    @Column(nullable = false)
    private Integer amount;

    // 쿠폰 이미지
    @Column(nullable = false, length = 1000)
    private String image;
}
