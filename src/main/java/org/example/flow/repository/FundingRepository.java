package org.example.flow.repository;

import org.example.flow.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FundingRepository extends JpaRepository<Funding, Integer> {
    List<Funding> findByStatus(Funding.STATUS status);

    Funding findByFundingId(Long fundingId);
}
