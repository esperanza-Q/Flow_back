package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "shop_info")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ShopInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_info_id")   // PK
    private Long shopInfoId;

    // FK (User와 연결) 1:1 관계 (단방향)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String location;   // 매장 위치

    @Column(columnDefinition = "TEXT")
    private String explanation;   // 매장 설명

    @Column(name = "now_month", nullable = false)
    private Integer nowMonth;   // 현재 월 (예: 202508)

    @Column(name = "month_payment", nullable = false)
    private Integer monthPayment;   // 이번 달 매출

    @Column(name = "partnership_cost", nullable = false)
    private Integer partnershipCost;   // 파트너쉽 비용

}
