package org.example.flow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rewardCoupon_id")
    @JsonBackReference
    private RewardCoupon rewardCoupon;

}
