package org.example.flow.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "benefit_req")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitReq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_req_id", nullable = false)
    private Long benefitReqId;   // PK

    // FK: ShopInfo (여러 적립 조건이 하나의 매장에 속함 → N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false)
    private ShopInfo shopInfo;

    // ENUM: SEED / COUPON
    @Enumerated(EnumType.STRING)
    @Column(name = "req_name", nullable = false, length = 20)
    private ReqName reqName;

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
    public enum ReqName {
        SEED, COUPON
    }

    @OneToMany(mappedBy = "benefitReq", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ReceiveVisitCoupon> receiveVisitCoupons;
}
