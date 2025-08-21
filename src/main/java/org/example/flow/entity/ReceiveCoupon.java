package org.example.flow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "receiveCoupon")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorColumn(name = "type") // 선택사항: DTYPE 대신 이름 지정
public class ReceiveCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="receiveCoupon_id", nullable = false)
    private Long receiveCouponId;

//    @Enumerated(EnumType.STRING)
//    private TYPE type;

    private LocalDateTime receiveAt;

    @PrePersist //jpa의 콜백 메서드. 엔터티가 처음 저장되기 직전에 실행. 즉, 새로운 row 생성시 현재 날짜 저장
    protected void onCreate() { this.receiveAt = LocalDateTime.now(); }

    private Boolean used = false;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


//    public enum TYPE {
//        REWARD,
//        VISIT,
//        RECOMMEND
//    }
}

