package com.margincalculator.repository;

import com.margincalculator.entity.PledgeClientwiseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PledgeClientwiseInventoryRepository extends JpaRepository<PledgeClientwiseInventory, Long> {

    List<PledgeClientwiseInventory> findByClientId(String clientId);

    /**
     * Sum the 60% eligible collateral value for a given client.
     */
    @Query("SELECT COALESCE(SUM(p.marketValue60Pct), 0) FROM PledgeClientwiseInventory p WHERE p.clientId = :clientId")
    BigDecimal sumMarketValue60PctByClientId(@Param("clientId") String clientId);
}
