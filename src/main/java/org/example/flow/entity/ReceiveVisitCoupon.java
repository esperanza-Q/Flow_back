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
@Table(name = "receive_visit_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("VISIT")
public class ReceiveVisitCoupon extends ReceiveCoupon {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_info_id")
    @JsonBackReference
    private ShopInfo shopInfo;
}
