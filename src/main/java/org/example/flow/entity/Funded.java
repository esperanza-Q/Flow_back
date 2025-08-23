package org.example.flow.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "funded")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Funded {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="funded_id", nullable = false)
    private Long fundedId;

    private LocalDateTime createdAt;

    @PrePersist //jpa의 콜백 메서드. 엔터티가 처음 저장되기 직전에 실행. 즉, 새로운 row 생성시 현재 날짜 저장
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    private Integer seeds;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "funding_id")
    @JsonBackReference
    private Funding funding;

}
