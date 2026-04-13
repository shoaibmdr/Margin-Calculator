package com.margincalculator.repository;

import com.margincalculator.entity.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {

    List<TradeLog> findByClientId(String clientId);

    List<TradeLog> findByClientIdAndTradeDate(String clientId, LocalDate tradeDate);

    List<TradeLog> findByTradeDateBetween(LocalDate startDate, LocalDate endDate);
}
