package com.margincalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a trade (buy/sell) performed by a client.
 * Columns: Date, ClientId, Scrips, TradeType, Quantity, InventoryImpact
 */
@Entity
@Table(name = "trade_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "client_id", nullable = false, length = 20)
    private String clientId;

    @Column(name = "scrips", nullable = false, length = 50)
    private String scrips;

    /**
     * BUY or SELL.
     */
    @Column(name = "trade_type", nullable = false, length = 10)
    private String tradeType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Positive value for BUY, negative for SELL.
     */
    @Column(name = "inventory_impact", nullable = false)
    private Integer inventoryImpact;
}
