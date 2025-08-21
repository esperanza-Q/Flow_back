package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "place") // ← 테이블명 올바르게
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="place_id", nullable = false)
    private Long placeId;

    // 한 장소는 한 매장만 → 1:1
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false, unique = true) // ← unique로 1:1 보장
    private ShopInfo shopInfo;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public enum Category {
        FOOD,
        CAFE,
        LIFE,
        FASHION,
        ETC
    }
}
