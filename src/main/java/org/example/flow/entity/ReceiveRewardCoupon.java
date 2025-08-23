package org.example.flow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Entity
@Table(name = "receiveRewardCoupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("REWARD")
public class ReceiveRewardCoupon extends ReceiveCoupon {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiveRewardCoupon_id")
    @JsonBackReference
    private RewardCoupon rewardCoupon;

//    @ManyToOne
//    @JoinColumn(name = "receiveCoupon_id")
//    @JsonBackReference
//    private RewardCoupon rewardCoupon;
}
