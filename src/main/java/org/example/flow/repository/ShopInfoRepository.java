package org.example.flow.repository;

import org.example.flow.entity.ShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ShopInfoRepository extends JpaRepository<ShopInfo, Integer> {
}
