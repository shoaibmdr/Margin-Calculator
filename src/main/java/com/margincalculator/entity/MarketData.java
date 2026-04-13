package com.margincalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents real-time / end-of-day market data for securities.
 * Columns: Id, Symbol, SecurityId, LTP, ClosePrice
 */
@Entity
@Table(name = "market_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, length = 50)
    private String symbol;

    @Column(name = "security_id", nullable = false, length = 50)
    private String securityId;

    /**
     * Last Traded Price.
     */
    @Column(name = "ltp", nullable = false, precision = 18, scale = 4)
    private BigDecimal ltp;

    @Column(name = "close_price", nullable = false, precision = 18, scale = 4)
    private BigDecimal closePrice;
}
