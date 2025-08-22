package org.example.flow.repository;

import org.example.flow.entity.BusinessHours;
import org.example.flow.entity.ShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    List<BusinessHours> findByShopInfo(ShopInfo shopInfo);
    void deleteByShopInfo(ShopInfo shopInfo);
}