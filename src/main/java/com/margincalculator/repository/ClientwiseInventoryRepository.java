package com.margincalculator.repository;

import com.margincalculator.entity.ClientwiseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ClientwiseInventoryRepository extends JpaRepository<ClientwiseInventory, Long> {

    List<ClientwiseInventory> findByClientId(String clientId);

    /**
     * Sum total market value for a given client across all their purchased holdings.
     */
    @Query("SELECT COALESCE(SUM(c.marketValue), 0) FROM ClientwiseInventory c WHERE c.clientId = :clientId")
    BigDecimal sumMarketValueByClientId(@Param("clientId") String clientId);
}
