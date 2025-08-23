package org.example.flow.repository;

import org.example.flow.entity.Funded;
import org.example.flow.entity.Funding;
import org.example.flow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundedRepository extends JpaRepository<Funded, Integer> {
    List<Funded> findByFunding(Funding funding);
    Boolean existsFundedByUser(User user);
}
