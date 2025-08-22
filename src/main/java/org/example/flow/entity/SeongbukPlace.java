package org.example.flow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// SeongbukPlace.java
@Entity
@Table(name = "seongbuk_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeongbukPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String category; // "음식", "카페" 등 한글 분류

    @Column(nullable=false)
    private String address;
}
