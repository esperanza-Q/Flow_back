package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benefit_req")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BenefitReq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_req_id")
    private Long pointReqId;   // PK

    // FK: ShopInfo (여러 적립 조건이 하나의 매장에 속함 → N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false)
    private ShopInfo shopInfo;

    // ENUM: SEED / COUPON
    @Enumerated(EnumType.STRING)
    @Column(name = "point_req_name", nullable = false, length = 20)
    private PointReqName pointReqName;

    // 상세 정보 (SEED일 때만 필요할 수 있음)
    @Column(name = "seed_detail")
    private String seedDetail;

    // 방문 횟수 조건 (nullable)
    @Column(name = "visit_count")
    private Integer visitCount;

    // 쿠폰 타입 (nullable)
    @Column(name = "coupon_type")
    private String couponType;

    // 쿠폰 이미지 (nullable)
    @Column(name = "coupon_image", length = 1000)
    private String couponImage;

    // ENUM 정의
    public enum PointReqName {
        SEED, COUPON
    }
}
