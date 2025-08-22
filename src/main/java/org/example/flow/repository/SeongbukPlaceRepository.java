package org.example.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.example.flow.entity.SeongbukPlace; // 기존 seongbuk_place 엔티티

public interface SeongbukPlaceRepository extends JpaRepository<SeongbukPlace, Integer> { }