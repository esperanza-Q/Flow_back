package org.example.flow.repository;

import org.example.flow.entity.ReceiveCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiveCouponRepository extends JpaRepository<ReceiveCoupon, Long> {
    List<ReceiveCoupon> findByUser_UserId(Long userId);
}
