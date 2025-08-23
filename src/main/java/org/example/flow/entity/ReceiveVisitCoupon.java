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
@Table(name = "receiveVisit_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("VISIT")
public class ReceiveVisitCoupon extends ReceiveCoupon {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "benefitReq_id")
    @JsonBackReference
    private BenefitReq benefitReq;
}
