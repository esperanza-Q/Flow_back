package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    // FK: User (한 명의 user는 하나의 프로필을 가짐 → 1:1 관계)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // Users 엔티티와 연결 (User 엔티티 이름에 맞게 수정 필요)

    // 지역 소비 (예: 금액 단위)
    @Column(name = "local_consumption", nullable = false)
    private Integer localConsumption = 0;

    // 펀딩 (예: 참여 횟수 or 금액)
    @Column(name = "funding", nullable = false)
    private Integer funding = 0;

    // 씨앗 (포인트성 자원)
    @Column(name = "seeds", nullable = false)
    private Integer seeds = 0;

    // 포인트
    @Column(name = "point", nullable = false)
    private Integer point = 0;
}
