package com.margincalculator.entity;

import com.margincalculator.dto.LimitStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Persistent monitoring snapshot that is upserted after every margin calculation run.
 *
 * <p>Table name: {@code margin_monitoring_snapshot}
 * One row per client; {@code last_modified_date} records when the snapshot was last refreshed.
 */
@Entity
@Table(name = "margin_monitoring_snapshot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarginMonitoringSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sequential number assigned at query time (not persisted). */
    @Transient
    private Integer sn;

    @Column(name = "client_id", nullable = false, length = 20, unique = true)
    private String clientId;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "market_value_purchased_shares", precision = 18, scale = 2)
    private BigDecimal marketValuePurchasedShares;

    @Column(name = "market_value_pledged_shares_60_pct", precision = 18, scale = 2)
    private BigDecimal marketValuePledgedShares60Pct;

    @Column(name = "total_market_value", precision = 18, scale = 2)
    private BigDecimal totalMarketValue;

    /** Loan Outstanding / Current Note Receivable. */
    @Column(name = "loan_outstanding", precision = 18, scale = 2)
    private BigDecimal loanOutstanding;

    @Column(name = "current_equity", precision = 18, scale = 2)
    private BigDecimal currentEquity;

    /** Current Margin % (NPR). */
    @Column(name = "current_equity_pct", precision = 7, scale = 2)
    private BigDecimal currentEquityPct;

    @Column(name = "initial_margin_pct", precision = 5, scale = 2)
    private BigDecimal initialMarginPct;

    @Column(name = "maintenance_margin_pct", precision = 5, scale = 2)
    private BigDecimal maintenanceMarginPct;

    @Column(name = "trigger_margin_pct", precision = 5, scale = 2)
    private BigDecimal triggerMarginPct;

    /** Status / Action: HEALTHY, MARGIN_CALL or FORCE_SELL. */
    @Column(name = "status_action", length = 20)
    private String statusAction;

    @Column(name = "shortfall_amount", precision = 18, scale = 2)
    private BigDecimal shortfallAmount;

    @Column(name = "client_pan", length = 20)
    private String clientPan;

    @Column(name = "client_mobile_number", length = 20)
    private String clientMobileNumber;

    @Column(name = "approved_margin_trading_limit", precision = 18, scale = 2)
    private BigDecimal approvedMarginTradingLimit;

    @Column(name = "approved_loan_limit", precision = 18, scale = 2)
    private BigDecimal approvedLoanLimit;

    @Column(name = "limit_approved_date")
    private LocalDate limitApprovedDate;

    @Column(name = "limit_expiry_date")
    private LocalDate limitExpiryDate;

    /** Days remaining before the trading limit expires (negative when already expired). */
    @Column(name = "days_remaining_before_expiry")
    private Long daysRemainingBeforeExpiry;

    @Column(name = "limit_status", length = 20)
    @Enumerated(EnumType.STRING)
    private LimitStatus limitStatus;

    /** Timestamp when this monitoring record was last updated. */
    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModifiedDate;
}
