package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")  // MySQL 예약어 user 피하기
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)   // PK
    private Long userId;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)   // ENUM을 문자열로 저장
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean admin = false;  // 기본값 false

    public enum Role {
        GENERAL,
        SHOP
    }
}
