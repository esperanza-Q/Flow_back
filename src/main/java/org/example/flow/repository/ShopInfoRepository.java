package org.example.flow.repository;

import org.example.flow.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopInfoRepository extends JpaRepository<ShopInfo, Long> { }

