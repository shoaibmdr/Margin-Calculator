package com.margincalculator.repository;

import com.margincalculator.entity.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {

    Optional<MarketData> findBySymbol(String symbol);

    Optional<MarketData> findBySecurityId(String securityId);
}
