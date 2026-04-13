package com.margincalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a client with their loan outstanding / receivable amount.
 */
@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @Column(name = "client_id", length = 20)
    private String clientId;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    /**
     * The outstanding loan amount that is receivable from the client.
     */
    @Column(name = "loan_outstanding", nullable = false, precision = 18, scale = 2)
    private BigDecimal loanOutstanding;

    /**
     * Initial Margin % – above this, the account is HEALTHY (default 50%).
     */
    @Column(name = "initial_margin_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal initialMarginPct;

    /**
     * Maintenance Margin % – below this a MARGIN_CALL is triggered (default 40%).
     */
    @Column(name = "maintenance_margin_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal maintenanceMarginPct;

    /**
     * Trigger Margin % – below this a FORCE_SELL is triggered (default 30%).
     */
    @Column(name = "trigger_margin_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal triggerMarginPct;
}
