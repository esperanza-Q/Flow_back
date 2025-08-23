package org.example.flow.repository;

import org.example.flow.entity.RewardCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardShopRepository extends JpaRepository<RewardCoupon, Long> {

}
