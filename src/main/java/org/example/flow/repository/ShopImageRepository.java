package org.example.flow.repository;

import org.example.flow.entity.ShopImage;
import org.example.flow.entity.ShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopImageRepository extends JpaRepository<ShopImage, Long> {
    List<ShopImage> findByShopInfo(ShopInfo shopInfo);
    void deleteByShopInfo(ShopInfo shopInfo);
}
