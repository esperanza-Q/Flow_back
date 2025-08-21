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

public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="place_id", nullable = false)
    private Long placeId;

    // FK: ShopInfo (한 장소는 한 매장만 가질 수 있음 → 1:1 관계)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false)
    private ShopInfo shopInfo;

//    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    private CATEGORY category;

//    @Column(nullable = false)
    private Double latitude;

//    @Column(nullable = false)
    private Double longitude;


    public enum CATEGORY {
        FOOD,
        CAFE,
        LIFE,
        FASHIOPN,
        ECT
    }

}
