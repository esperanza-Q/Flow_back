package org.example.flow.repository;

import org.example.flow.entity.BenefitReq;
import org.example.flow.entity.ShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BenefitReqRepository extends JpaRepository<BenefitReq, Long> {
    List<BenefitReq> findByShopInfo(ShopInfo shopInfo);

    Optional<BenefitReq> findFirstByShopInfoAndReqNameOrderByBenefitReqIdDesc(
            ShopInfo shopInfo, BenefitReq.ReqName reqName
    );
    void deleteByShopInfoAndReqName(ShopInfo shopInfo, BenefitReq.ReqName reqName);
}
