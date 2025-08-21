package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_image_id")
    private Long shopImageId;   // PK

    // FK: ShopInfo (한 매장은 여러 이미지를 가질 수 있음 → N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false)
    private ShopInfo shopInfo;

    // 이미지 경로
    @Column(nullable = false, length = 1000)
    private String image;
}
