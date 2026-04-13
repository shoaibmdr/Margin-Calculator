package com.margincalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents the client-wise pledged share inventory.
 * Columns: ClientId, Scrips, Total Quantity, LTP, MarketValue, 60% of MarketValue
 */
@Entity
@Table(name = "pledge_clientwise_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PledgeClientwiseInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, length = 20)
    private String clientId;

    @Column(name = "scrips", nullable = false, length = 50)
    private String scrips;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    /**
     * Last Traded Price for the pledged scrip.
     */
    @Column(name = "ltp", nullable = false, precision = 18, scale = 4)
    private BigDecimal ltp;

    /**
     * Market value = total_quantity * ltp.
     */
    @Column(name = "market_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal marketValue;

    /**
     * Eligible collateral value = 60% of market value.
     */
    @Column(name = "market_value_60_pct", nullable = false, precision = 18, scale = 2)
    private BigDecimal marketValue60Pct;
}
