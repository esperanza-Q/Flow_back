package org.example.flow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendShop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recommendShop_id", nullable = false)
    private Long recommendShopId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "shop_info_id")
    @JsonBackReference
    private ShopInfo shopInfo;

    private String recommendInfo;

    private LocalDateTime createdAt;

    @PrePersist //jpa의 콜백 메서드. 엔터티가 처음 저장되기 직전에 실행. 즉, 새로운 row 생성시 현재 날짜 저장
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    private String comment1;
    private String comment2;
    private String comment3;

    private Boolean visited = false;

    private LocalDateTime visitedAt;

}
