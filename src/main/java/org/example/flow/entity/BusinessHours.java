package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "business_hours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_hours_id")
    private Long businessHoursId;   // PK

    // FK: ShopInfo (한 매장에는 여러 개의 영업시간이 있을 수 있음 → N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_info_id", nullable = false)
    private ShopInfo shopInfoId;

    // 요일 (ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Week week;

    // 오픈 시간
    @Column(name = "open_time", nullable = false)
    private Date openTime;   // ex) "09:00"

    // 마감 시간
    @Column(name = "close_time", nullable = false)
    private Date closeTime;  // ex) "18:00"

    // 내부 ENUM: 요일
    public enum Week {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
}
