package com.margincalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents the client-wise inventory of purchased/held shares.
 * Columns: ClientId, Scrips, Total Quantity, MarketValue
 */
@Entity
@Table(name = "clientwise_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientwiseInventory {

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
     * Current market value of the holding (total_quantity * LTP).
     */
    @Column(name = "market_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal marketValue;
}
